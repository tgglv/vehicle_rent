package com.timur.rent.util;

public class RentVehicleRepository {

    // TODO: доделать если данный класс будет использоваться

    private static RentVehicleRepository instance = null;

    protected RentVehicleRepository() {

    }

    public RentVehicleRepository getInstance() {
        if (null == instance) {
            instance = new RentVehicleRepository();
        }
        return instance;
    }

//    public
}
