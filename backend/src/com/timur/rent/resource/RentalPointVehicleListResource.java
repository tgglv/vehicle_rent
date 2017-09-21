package com.timur.rent.resource;

import com.google.gson.Gson;
import com.timur.rent.util.Connection;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class RentalPointVehicleListResource extends ServerResource {

    public static final int IS_AVAILABLE = 1;

    @Get("json")
    public String fetchAllRentalPointAvailableVehicles() {
        String rentPointId = (String) getRequest().getAttributes().get("point_id");
        String type = (String) getRequest().getAttributes().get("type");
        String vendor = (String) getRequest().getAttributes().get("vendor");

        if (null == rentPointId) {
            getResponse().setStatus(new Status(400));
            return "{\"error\": \"Point ID not specified\"}";
        }

        Integer vendorId = null;
        try {
            vendorId = Integer.parseInt(vendor);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (null != type) {
            String[] availableTypes = new String[]{"car", "bike", "scooter"};
            boolean found = false;
            for (String currentType : availableTypes) {
                if (currentType.equals(type)) {
                    found = true;
                }
            }
            if (!found) {
                type = null;
            }
        }


        String sqlQuery = "SELECT " +
                "v.id,\n" +
                "v.type,\n" +
                "ve.name AS vendor_name,\n" +
                "v.name AS vehicle_name,\n" +
                "v.license_plate\n" +
                "FROM car_rent.rental_point_vehicle rpv\n" +
                "JOIN car_rent.vehicle v ON rpv.id_vehicle = v.id\n" +
                "JOIN car_rent.vendor ve ON ve.id = v.id_vendor\n" +
                "WHERE " +
                "id_rent_point = ? AND available = 1\n";

        if (null != type) {
            sqlQuery += "AND v.type = ?\n";
        }
        if (null != vendorId) {
            sqlQuery += "AND v.id_vendor = ?";
        }

        try {

            PreparedStatement statement = Connection.getInstance().getConnection().prepareStatement(
                    sqlQuery
            );
            int index = 1;
            statement.setInt(1, Integer.parseInt(rentPointId));
            if (null != type) {
                ++index;
                statement.setString(index, type);
            }
            if (null != vendorId) {
                ++index;
                statement.setInt(index, vendorId);
            }

            ResultSet rs = statement.executeQuery();

            HashMap<Integer, HashMap<String, String>> map = new HashMap<Integer, HashMap<String, String>>();
            if (null != rs) {
                while (rs.next()) {
                    HashMap<String, String> vehicleMap = new HashMap<String, String>();
                    vehicleMap.put("vehicle_type", rs.getString("type"));
                    vehicleMap.put("vendor_name", rs.getString("vendor_name"));
                    vehicleMap.put("vehicle_name", rs.getString("vehicle_name"));
                    vehicleMap.put("license_plate", rs.getString("license_plate"));

                    map.put(rs.getInt("id"), vehicleMap);
                }

                getResponse().setStatus(new Status(200));
                return new Gson().toJson(map);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        getResponse().setStatus(new Status(404));
        return "[]";
    }
}
