<?php

/*
 * Обновление координат арендованных ТС
 *
 * Принебрежем тем находится ТС в данный момент в прокате или нет.
 * То есть для упрощения задачи будем "двигать" все ТС.
 *
 * Кроме того, не будем учитывать в каком пункте проката они находятся.
 * Все ТС будут совершать движение около пункта проката, в котором они находились
 * на момент запуска скрипта.
 *
 * Периодически, будут загружаться обновления данных о ТС
 * */

class VehicleLocationsUpdater
{
    private $host = 'localhost';
    private $port = 3306;
    private $user = 'dev';
    private $password = 'dev';
    private $database = 'car_rent';

    private $connection;

    const BASE_URL = 'http://localhost:8888/vehiclerent';

    public function __construct()
    {
        // TODO: подключить скрипт к репликации
    }

    public function run()
    {
        $counter = 0;
        $vehicleList = [];
        $keys = [];
        while (true) {
            // Раз в минуту будем обновлять данные об арендованных машинах
            if (0 == $counter) {
                $vehicleList = $this->getVehicles();
                $keys = array_keys($vehicleList);
                foreach ($keys as $key) {
                    $vehicle = &$vehicleList[$key];
                    $this->loadLocation($vehicle);
                }
            }

            // Регулярно меняем координаты у ТС, находящихся в аренде
            foreach ($keys as $key) {
                $vehicle = &$vehicleList[$key];

                if ($vehicle->isRentAvailable()) {
                    // Не делаем запрос к API, т.к. ТС стоит в прокате
                    $vehicle->setRentPointLocation();
                } else {
                    $this->changeLocation($vehicle);
                }
                $this->saveLocation($vehicle);
            }

            sleep(10);

            ++$counter;
            if ($counter > 6) {
                $counter = 0;
            }
        }
    }

    /** @return Vehicle[] */
    private function getVehicles()
    {
        $connection = mysqli_connect($this->host, $this->user, $this->password, $this->database, $this->port);
        if (!$connection) {
            throw new \Exception('Cannot connect to database');
        }
        mysqli_query($connection, "SET NAMES 'UTF8'");

        /* Возможна ситуация: машина вернулась в новый пункт проката, но демон делает отметки рядом с прежными пунктом
        Для того чтобы это не перешло в проблему мы будем получать данных обо всех машинах (в прокате и вернувшихся).
        У машин, стоящих в пункте проката будут проставлять координаты проката.
        */

        $sql = <<< SQL
SELECT
	v.id AS vehicle_id,
	pv.available,
	p.latitude,
	p.longitude
FROM car_rent.rental_point_vehicle pv
JOIN car_rent.vehicle v ON pv.id_vehicle = v.id
JOIN car_rent.rental_point p ON pv.id_rent_point = p.id;
SQL;

        $queryResult = mysqli_query($connection, $sql, MYSQLI_ASSOC);
        if (!$queryResult) {

            #debug // TODO: убрать отладку
            var_dump(mysqli_errno($this->connection), mysqli_error($this->connection));
            die;

        }
        $result = [];
        while (($row = $queryResult->fetch_assoc())) {
            $result[] = new Vehicle($row);
        }

        mysqli_free_result($queryResult);
        mysqli_close($connection);

        return $result;
    }

    private function getUrl(Vehicle $vehicle)
    {
        return self::BASE_URL . '/tracking?vehicle_id=' . $vehicle->getId();
    }

    private function loadLocation(Vehicle &$vehicle)
    {
        $ch = curl_init($this->getUrl($vehicle));
        curl_setopt_array(
            $ch,
            [
                CURLOPT_RETURNTRANSFER => true,
                CURLOPT_TIMEOUT => 10,
            ]
        );

        $result = curl_exec($ch);

        if (!$result) {

            #debug
            var_dump(__FUNCTION__, __LINE__, curl_getinfo($ch));
            die;

        }

        $data = null;

        if (200 == curl_getinfo($ch, CURLINFO_HTTP_CODE)) {
            $data = json_decode($result, true);
            if (JSON_ERROR_NONE != json_last_error()
                || !isset($data[Point::LATITUDE]) || !isset($data[Point::LONGITUDE])
            ) {

                # debug
                var_dump(
                    __FUNCTION__,
                    __LINE__,
                    $result,
                    json_last_error(),
                    json_last_error_msg(),
                    curl_getinfo($ch, CURLINFO_HTTP_CODE)
                );
                die;

            }
        } else {
            // в Redis нет данных о месте расположения ТС. Применим расположение пункта проката
            $vehicle->setRentPointLocation();
            $data = [
                Point::LATITUDE => $vehicle->getLocation()->getLatitude(),
                Point::LONGITUDE => $vehicle->getLocation()->getLongitude(),
            ];
        }

        $newLocation = new Point;
        $newLocation->setLatitude((float)$data[Point::LATITUDE]);
        $newLocation->setLongitude((float)$data[Point::LONGITUDE]);
        $vehicle->setLocation($newLocation);
    }

    private function changeLocation(Vehicle &$vehicle)
    {
        $point = $vehicle->getLocation();

        $point->setLatitude($this->randomize($point->getLatitude()));
        $point->setLongitude($this->randomize($point->getLongitude()));

        $vehicle->setLocation($point);
    }

    private function randomize($value)
    {
        $random = (float)rand(0, 10000) / 10000;
        return $value + (0.5 - $random) / 100;
    }

    private function saveLocation(Vehicle $vehicle)
    {
        $url = $this->getUrl($vehicle) . '?' . http_build_query(
                [
                    Point::LATITUDE => $vehicle->getLocation()->getLatitude(),
                    Point::LONGITUDE => $vehicle->getLocation()->getLongitude(),
                ]
            );

        $ch = curl_init($url);
        curl_setopt_array(
            $ch,
            [
                CURLOPT_RETURNTRANSFER => true,
                CURLOPT_POST => true,
                CURLOPT_TIMEOUT => 60,
            ]
        );

        $result = curl_exec($ch);
        if (204 != curl_getinfo($ch, CURLINFO_HTTP_CODE)) {

            # debug
            var_dump(__FUNCTION__, __LINE__, $result, curl_getinfo($ch), curl_errno($ch), curl_error($ch));
            die;

        }
    }
}

class Vehicle
{
    private $id;
    private $isAvailable;
    /** @var Point */
    private $rentPointLocation;
    /** @var Point */
    private $location;
    /** @var boolean */
    private $isInRentPoint;

    public function __construct(array $data)
    {
        $this->id = (int)$data['vehicle_id'];
        $this->isAvailable = (int)$data['available'];
        $this->isInRentPoint = false;

        $this->location = new Point;

        $this->rentPointLocation = new Point;
        $this->rentPointLocation->setLatitude((float)$data[Point::LATITUDE]);
        $this->rentPointLocation->setLongitude((float)$data[Point::LONGITUDE]);
    }

    public function isRentAvailable()
    {
        return 1 === $this->isAvailable;
    }

    public function getLocation()
    {
        return $this->location;
    }

    public function setLocation(Point $location)
    {
        $this->location = $location;
    }

    public function setRentPointLocation()
    {
        $this->location->setLatitude($this->rentPointLocation->getLatitude());
        $this->location->setLongitude($this->rentPointLocation->getLongitude());
        $this->isInRentPoint = true;
    }

    public function getId()
    {
        return $this->id;
    }
}

class Point
{
    const LATITUDE = 'latitude';
    const LONGITUDE = 'longitude';

    private $latitude;
    private $longitude;

    /**
     * @return mixed
     */
    public function getLatitude()
    {
        return $this->latitude;
    }

    /**
     * @param mixed $latitude
     */
    public function setLatitude($latitude)
    {
        $this->latitude = $latitude;
    }

    /**
     * @return mixed
     */
    public function getLongitude()
    {
        return $this->longitude;
    }

    /**
     * @param mixed $longitude
     */
    public function setLongitude($longitude)
    {
        $this->longitude = $longitude;
    }


}

$updater = new VehicleLocationsUpdater;
$updater->run();