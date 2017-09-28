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

public class RentHistoryServlet extends EnhancedServlet {

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

        String vehicleType = parseVehicleType(httpServletRequest.getParameter("type"));
        String licensePlate = httpServletRequest.getParameter("license_plate");
        String dateRentFrom = assertSqlDate(httpServletRequest.getParameter("date_rent_from"));
        String dateRentTo = assertSqlDate(httpServletRequest.getParameter("date_rent_to"));
        String dateReturnFrom = assertSqlDate(httpServletRequest.getParameter("date_return_from"));
        String dateReturnTo = assertSqlDate(httpServletRequest.getParameter("date_return_to"));
        String customer = httpServletRequest.getParameter("customer");

        Integer vendorId = parseInteger(httpServletRequest.getParameter("vendor_id"));
        Integer rentPointId = parseInteger(httpServletRequest.getParameter("rent_point_id"));
        Integer returnPointId = parseInteger(httpServletRequest.getParameter("return_point_id"));
        Integer showInRentVehiclesOnly = parseInteger(httpServletRequest.getParameter("show_in_rent_vehicle_only"));

        String sqlQuery = getSqlQueryPattern();

        boolean searchByVehicleType = searchByParam(vehicleType);

        boolean searchByVendor = searchByParam(vendorId);

        boolean searchByLicensePlate = searchByParam(licensePlate);

        boolean searchByDateRentFrom = searchByParam(dateRentFrom);
        boolean searchByDateRentTo = searchByParam(dateRentTo);
        boolean searchByDateReturnFrom = searchByParam(dateReturnFrom);
        boolean searchByDateReturnTo = searchByParam(dateReturnTo);

        boolean searchByCustomer = searchByParam(customer);

        boolean searchByRentPointId = searchByParam(rentPointId);
        boolean searchByReturnPointId = searchByParam(returnPointId);

        boolean searchOnlyInRentVehicles = searchByParam(showInRentVehiclesOnly);

        if (searchByVehicleType) {
            sqlQuery += "AND vehicle.type = ?\n";
        }
        if (searchByVendor) {
            sqlQuery += "AND vehicle.id_vendor = ?\n";
        }
        if (searchByLicensePlate) {
            sqlQuery += "AND vehicle.license_plate LIKE CONCAT('%', ?, '%')\n";
        }
        if (searchByDateRentFrom) {
            sqlQuery += "AND vr.rent_time >= CONCAT(?, ' 00:00:00')\n";
        }
        if (searchByDateRentTo) {
            sqlQuery += "AND vr.rent_time <= CONCAT(?, '23:59:59')\n";
        }
        if (searchByDateReturnFrom) {
            sqlQuery += "AND vr.return_time >= CONCAT(?, ' 00:00:00')\n";
        }
        if (searchByDateReturnTo) {
            sqlQuery += "AND vr.return_time <= CONCAT(?, ' 23:59:59')\n";
        }
        if (searchByCustomer) {
            sqlQuery += "AND customer.name LIKE CONCAT('%', ?, '%')\n";
        }
        if (searchByRentPointId) {
            sqlQuery += "AND vr.id_rent_point = ?\n";
        }
        if (searchByReturnPointId) {
            sqlQuery += "AND vr.id_return_point = ?\n";
        }
        if (searchOnlyInRentVehicles) {
            sqlQuery += "AND vr.id_return_point IS NULL\n";
        }

        sqlQuery += "ORDER BY vr.id ASC";

        int index = 0;
        try {
            PreparedStatement statement = DbConnection.getInstance().getConnection().prepareStatement(sqlQuery);

            if (searchByVehicleType) {
                statement.setString(++index, vehicleType);
            }
            if (searchByVendor) {
                statement.setInt(++index, vendorId);
            }
            if (searchByLicensePlate) {
                statement.setString(++index, licensePlate);
            }
            if (searchByDateRentFrom) {
                statement.setString(++index, dateRentFrom);
            }
            if (searchByDateRentTo) {
                statement.setString(++index, dateRentTo);
            }
            if (searchByDateReturnFrom) {
                statement.setString(++index, dateReturnFrom);
            }
            if (searchByDateReturnTo) {
                statement.setString(++index, dateReturnTo);
            }
            if (searchByCustomer) {
                statement.setString(++index, customer);
            }
            if (searchByRentPointId) {
                statement.setInt(++index, rentPointId);
            }
            if (searchByReturnPointId) {
                statement.setInt(++index, returnPointId);
            }
            ResultSet rs = statement.executeQuery();
            if (null != rs) {
                HashMap<Integer, HashMap<String, String>> map = new HashMap<Integer, HashMap<String, String>>();
                String[] fields = getColumnNames(rs);
                if (null == fields) {
                    setSomethingWentWrongResponse(httpServletResponse);
                    return;
                }
                while (rs.next()) {
                    HashMap<String, String> vehicleData = new HashMap<String, String>();

                    for (String field : fields) {
                        vehicleData.put(field, rs.getString(field));
                    }

                    map.put(rs.getInt("rent_record_id"), vehicleData);
                }

                setResponse(httpServletResponse, 200, new Gson().toJson(map));
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        setSomethingWentWrongResponse(httpServletResponse);
    }

    private String getSqlQueryPattern() {
        return "SELECT\n" +
                "  vr.id AS rent_record_id,\n" +
                "  vehicle.id AS vehicle_id,\n" +
                "  vehicle.type AS vehicle_type,\n" +
                "  vendor.name AS vendor_name,\n" +
                "  vehicle.name AS vehicle_name,\n" +
                "  vehicle.license_plate,\n" +
                "  customer.name AS customer_name,\n" +
                "  country.name AS customer_country_name,\n" +
                "  rent_point.name AS rent_point_name,\n" +
                "  return_point.name AS return_point_name,\n" +
                "  DATE(rent_time) AS rent_date,\n" +
                "  DATE(return_time) AS return_date\n" +
                "FROM car_rent.vehicle_rent vr\n" +
                "JOIN car_rent.vehicle vehicle ON vr.id_vehicle = vehicle.id\n" +
                "JOIN car_rent.vendor vendor ON vendor.id = vehicle.id_vendor\n" +
                "JOIN car_rent.customer customer ON customer.id = vr.id_customer\n" +
                "JOIN car_rent.country country ON country.id = customer.id_country\n" +
                "JOIN car_rent.rental_point rent_point ON rent_point.id = vr.id_rent_point\n" +
                "LEFT JOIN car_rent.rental_point return_point ON return_point.id = vr.id_return_point\n" +
                "WHERE 1\n";
    }
}
