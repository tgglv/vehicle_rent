package com.timur.rent.resource;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class HistoryGeneralSearchResource extends ServerResource {

    @Get("json")
    public String getHistory() {
        /*SELECT
	vehicle.id AS vehicle_id,
	vehicle.type AS vehicle_type,
	vendor.name AS vendor_name,
	vehicle.name AS vehicle_name,
	vehicle.license_plate,
	customer.name AS customer_name,
	country.name AS customer_country_name,
	rent_point.name AS rent_point_name,
	return_point.name AS return_point_name,
	DATE(rent_time) AS rent_date,
	DATE(return_time) AS return_date
FROM car_rent.vehicle_rent vr
JOIN car_rent.vehicle vehicle ON vr.id_vehicle = vehicle.id
JOIN car_rent.vendor vendor ON vendor.id = vehicle.id_vendor
JOIN car_rent.customer customer ON customer.id = vr.id_customer
JOIN car_rent.country country ON country.id = customer.id_country
JOIN car_rent.rental_point rent_point ON rent_point.id = vr.id_rent_point
JOIN car_rent.rental_point return_point ON return_point.id = vr.id_return_point
WHERE 1
#	AND ...
;*/

        return ""; // TODO
    }

}
