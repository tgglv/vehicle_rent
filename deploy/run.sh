#!/bin/bash

nginx

php-fpm7

/opt/tomcat/bin/startup.sh

mysql_install_db
chown -Rv mysql /app/data
chmod -Rv 0777 /app/data
sudo -u mysql /usr/bin/mysqld_safe --datadir='/app/data' &
sleep 10 && \
    mysql < /app/mysql_user.sql && \
    mysql < /app/car_rent.sql

redis-server &

# Имитация отправки машинами
php /app/vehicle_rent/frontend/cron/vehicleLocationsUpdater.php