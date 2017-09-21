package com.timur.rent.resource;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.restlet.service.CorsService;

import java.util.Arrays;
import java.util.HashSet;

public class RentServerApp extends Application {

    final static int SERVER_PORT = 8000;

    public static void main(String[] args) throws Exception {
        Component component = new Component();
        component.getServers().add(Protocol.HTTP, SERVER_PORT);
        component.getDefaultHost().attach(new RentServerApp());

        CorsService corsService = new CorsService();
        corsService.setAllowedOrigins(new HashSet(Arrays.asList("*")));
        corsService.setAllowedCredentials(true);

        component.getServices().add(corsService);
        component.start();
    }

    @Override
    public Restlet createInboundRoot() {
        Router router = new Router(getContext());

        router.attach("/country/list", CountryResource.class);
        router.attach("/rental_point/list", RentalPointResource.class);
        router.attach("/rental_point/{point_id}/vehicle/list", RentalPointVehicleListResource.class);
        router.attach("/vehicle/type/list", VehicleTypesResource.class);
        router.attach("/vehicle/vendor/list", VehicleVendorsResource.class);
        router.attach("/customer/list", CustomerList.class);

        router.attach("/rental_point/{point_id}/vehicle/{vehicle_id}/customer/{customer_id}", RentalPointRentResource.class); // POST - выдать ТС
        router.attach("/vehicle/return/list", RentalPointReturnResource.class); // GET - список арендованных ТС
        router.attach("/rental_point/{point_id}/rent_record_id/{rent_record_id}", RentalPointReturnResource.class); // POST - принять ТС

        router.attach("/history", HistoryGeneralSearchResource.class);

        /*
        router.attach("/customer", CustomerResource.class); // TODO: POST, добавить клиента
*/
        // Добавить вызовы по остальному flow

        return router;
    }
}
