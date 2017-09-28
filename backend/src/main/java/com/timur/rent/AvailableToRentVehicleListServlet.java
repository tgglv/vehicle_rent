package com.timur.rent;

import com.google.gson.Gson;
import com.timur.rent.storage.DbConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class AvailableToRentVehicleListServlet extends EnhancedServlet {
    public static final int IS_AVAILABLE = 1;

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        Integer rentPointId = parseInteger(httpServletRequest.getParameter("point_id"));
        String vehicleType = parseVehicleType(httpServletRequest.getParameter("type"));
        Integer vendorId = parseInteger(httpServletRequest.getParameter("vendor"));

        if (!searchByParam(rentPointId)) {
            setResponse(httpServletResponse, 400, "{\"error\": \"Point ID not specified\"}");
            return;
        }

        String sqlQuery = "SELECT " +
                "  v.id,\n" +
                "  v.type,\n" +
                "  ve.name AS vendor_name,\n" +
                "  v.name AS vehicle_name,\n" +
                "  v.license_plate\n" +
                "FROM car_rent.rental_point_vehicle rpv\n" +
                "JOIN car_rent.vehicle v ON rpv.id_vehicle = v.id\n" +
                "JOIN car_rent.vendor ve ON ve.id = v.id_vendor\n" +
                "WHERE " +
                "  id_rent_point = ? AND available = 1\n";

        boolean searchByVehicleType = searchByParam(vehicleType);
        boolean searchByVendorId = searchByParam(vendorId);

        if (searchByVehicleType) {
            sqlQuery += "AND v.type = ?\n";
        }
        if (searchByVendorId) {
            sqlQuery += "AND v.id_vendor = ?\n";
        }

        try {
            PreparedStatement statement = DbConnection.getInstance().getConnection().prepareStatement(sqlQuery);

            int index = 1;
            statement.setInt(1, rentPointId);
            if (searchByVehicleType) {
                ++index;
                statement.setString(index, vehicleType);
            }
            if (searchByVendorId) {
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

                setResponse(httpServletResponse, 200, new Gson().toJson(map));
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        setResponse(httpServletResponse, 404, "[]");
    }
}
