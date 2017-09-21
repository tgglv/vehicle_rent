package com.timur.rent.resource;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class VehicleTypesResource extends ServerResource {

    @Get("json")
    public String getTypes() {
        return "{\"car\": \"Автомобиль\", \"bike\": \"Мотоцикл\", \"scooter\": \"Скутер\"}";
    }

}
