package com.timur.rent;

import com.google.gson.Gson;
import com.timur.rent.storage.DbConnection;
import com.timur.rent.storage.RedisConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class ReturnVehicleServlet extends EnhancedServlet {

    class ReturnModel {
        private int vehicleId;
        private int vehicleRentRecordId;
        private int previousRentPointId;
        private int currentRentPointId;

        public ReturnModel(int vehicleId, int vehicleRentRecordId, int previousRentPointId, int currentRentPointId) {
            this.vehicleId = vehicleId;
            this.vehicleRentRecordId = vehicleRentRecordId;
            this.previousRentPointId = previousRentPointId;
            this.currentRentPointId = currentRentPointId;
        }

        public int getVehicleId() {
            return vehicleId;
        }

        public int getVehicleRentRecordId() {
            return vehicleRentRecordId;
        }

        public Integer getPreviousRentPointId() {
            return previousRentPointId;
        }

        public int getCurrentRentPointId() {
            return currentRentPointId;
        }
    }

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        String licensePlate = httpServletRequest.getParameter("license_plate");
        String vehicleType = parseVehicleType(httpServletRequest.getParameter("type"));
        Integer vendorId = parseInteger(httpServletRequest.getParameter("vendor"));
        String vehicleName = httpServletRequest.getParameter("name");
        String customerName = httpServletRequest.getParameter("customer");

        String sqlQuery = "SELECT \n" +
                "vr.id AS rent_record_id,\n" +
                "v.id AS vehicle_id,\n" +
                "v.type AS vehicle_type,\n" +
                "vendor.name AS vendor_name,\n" +
                "v.name AS vehicle_name,\n" +
                "v.license_plate,\n" +
                "vr.rent_time,\n" +
                "customer.name AS customer_name,\n" +
                "country.name AS country_name\n" +
                "FROM car_rent.rental_point p\n" +
                "JOIN car_rent.rental_point_vehicle pv ON p.id = pv.id_rent_point\n" +
                "JOIN car_rent.vehicle v ON pv.id_vehicle = v.id\n" +
                "JOIN car_rent.vendor vendor ON vendor.id = v.id_vendor\n" +
                "JOIN car_rent.vehicle_rent vr ON (p.id, v.id) = (vr.id_rent_point, vr.id_vehicle)\n" +
                "JOIN car_rent.customer customer ON vr.id_customer = customer.id\n" +
                "JOIN car_rent.country ON customer.id_country = country.id\n" +
                "WHERE 1\n";

        boolean searchByLicensePlate = searchByParam(licensePlate);
        boolean searchByCustomerName = searchByParam(customerName);
        boolean searchByVehicleName = searchByParam(vehicleName);
        boolean searchByVendorId = searchByParam(vendorId);
        boolean searchByVehicleType = searchByParam(vehicleType);

        if (searchByLicensePlate) {
            sqlQuery += "AND v.license_plate LIKE concat('%', ?, '%')\n";
        }
        if (searchByCustomerName) {
            sqlQuery += "AND customer.name LIKE concat('%', ?, '%') \n";
        }
        if (searchByVehicleName) {
            sqlQuery += "AND v.name LIKE concat('%', ?, '%')\n";
        }
        if (searchByVendorId) {
            sqlQuery += "AND vendor.id = ?\n";
        }
        if (searchByVehicleType) {
            sqlQuery += "AND v.type = ?";
        }

        sqlQuery += "AND vr.id_return_point IS NULL\n" +
                "ORDER BY rent_time";

        int index = 0;
        try {
            PreparedStatement statement = DbConnection.getInstance().getConnection().prepareStatement(sqlQuery);
            if (searchByLicensePlate) {
                statement.setString(++index, licensePlate);
            }
            if (searchByCustomerName) {
                statement.setString(++index, customerName);
            }
            if (searchByVehicleName) {
                statement.setString(++index, vehicleName);
            }
            if (searchByVendorId) {
                statement.setInt(++index, vendorId);
            }
            if (searchByVehicleType) {
                statement.setString(++index, vehicleType);
            }

            ResultSet rs = statement.executeQuery();
            if (null != rs) {
                HashMap<Integer, HashMap<String, String>> map = new HashMap<Integer, HashMap<String, String>>();
                while (rs.next()) {
                    HashMap<String, String> rentInfo = new HashMap<String, String>();
                    rentInfo.put("vehicle_id", rs.getString("vehicle_id"));
                    rentInfo.put("vehicle_type", rs.getString("vehicle_type"));
                    rentInfo.put("vendor_name", rs.getString("vendor_name"));
                    rentInfo.put("vehicle_name", rs.getString("vehicle_name"));
                    rentInfo.put("license_plate", rs.getString("license_plate"));
                    rentInfo.put("rent_time", rs.getString("rent_time"));
                    rentInfo.put("customer_name", rs.getString("customer_name"));
                    rentInfo.put("country_name", rs.getString("country_name"));

                    map.put(rs.getInt("rent_record_id"), rentInfo);
                }

                setResponse(httpServletResponse, 200, new Gson().toJson(map));
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setSomethingWentWrongResponse(httpServletResponse);
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        Integer pointId = parseInteger (httpServletRequest.getParameter("point_id"));
        Integer rentRecordId = parseInteger(httpServletRequest.getParameter("rent_record_id"));

        if (!searchByParam(pointId) || !searchByParam(rentRecordId)) {
            setResponse(httpServletResponse, 403, "[]");
            return;
        }

        ReturnModel model = getReturnModelBy(pointId, rentRecordId);
        if (null == model) {
            setResponse(httpServletResponse, 404, "[]");
            return;
        }

        java.sql.Connection connection = null;
        try {
            connection = DbConnection.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (null != connection) {
            try {
                connection.setAutoCommit(false);
                if (setEndOfVehicleRent(model)) {
                    boolean success = false;
                    if (pointId.equals(model.getPreviousRentPointId())) {
                        success = setVehicleAvailableInTheSameRentalPoint(model);
                    } else if (setVehicleAvailableInNewRentalPoint(model)) {
                        success = removeVehicleFromOriginRentalPoint(model);
                    }

                    if (success) {
                        connection.commit();
                        connection.setAutoCommit(true);

                        locateToCurrentRentalPoint(model);

                        setResponse(httpServletResponse, 200, "{\"status\": \"OK\"}");
                        return;
                    }
                }
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException e2) {
                    e2.printStackTrace();
                }
                e.printStackTrace();
            }
        }

        setSomethingWentWrongResponse(httpServletResponse);
    }

    private void locateToCurrentRentalPoint(ReturnModel model) {
        try {
            PreparedStatement statement = DbConnection.getInstance().getConnection().prepareStatement(
                    "SELECT latitude, longitude FROM car_rent.rental_point WHERE id = ?"
            );
            statement.setInt(1, model.getCurrentRentPointId());
            ResultSet rs = statement.executeQuery();

            if (null != rs && rs.next()) {
                HashMap<String, Float> map = new HashMap<String, Float>();
                map.put("latitude", rs.getFloat("latitude"));
                map.put("longitude", rs.getFloat("longitude"));

                String json = new Gson().toJson(map);

                RedisConnection.getInstance().getConnection().set("vehicle:id:" + model.getVehicleId(), json);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ReturnModel getReturnModelBy(int currentRentPointId, Integer rentReturnId) {
        try {
            PreparedStatement statement = DbConnection.getInstance().getConnection().prepareStatement(
                    "SELECT\n" +
                            "    v.id AS vehicle_id,\n" +
                            "    vr.id_rent_point AS prev_rent_point_id\n" +
                            "FROM car_rent.vehicle v\n" +
                            "JOIN car_rent.vehicle_rent vr ON vr.id_vehicle = v.id\n" +
                            "WHERE\n" +
                            "    vr.id = ?\n" +
                            "    AND id_rent_point IS NOT NULL\n" +
                            "    AND id_return_point IS NULL");
            statement.setInt(1, rentReturnId);
            ResultSet rs = statement.executeQuery();
            if (null != rs && rs.next()) {
                // уникальный индекс по гос. номеру ТС гарантирует, что если ТС будет найдено, то оно будет уникальным
                return new ReturnModel(
                        rs.getInt("vehicle_id"),
                        rentReturnId,
                        rs.getInt("prev_rent_point_id"),
                        currentRentPointId
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean setEndOfVehicleRent(ReturnModel model) {
        try {
            PreparedStatement statement = DbConnection.getInstance().getConnection().prepareStatement(
                    "UPDATE car_rent.vehicle_rent SET id_return_point = ?, return_time = NOW() WHERE id = ?"
            );
            statement.setInt(1, model.getCurrentRentPointId());
            statement.setInt(2, model.getVehicleRentRecordId());
            if (1 == statement.executeUpdate()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean setVehicleAvailableInTheSameRentalPoint(ReturnModel model) {
        try {
            PreparedStatement statement = DbConnection.getInstance().getConnection().prepareStatement(
                    "UPDATE car_rent.rental_point_vehicle SET available = 1 WHERE id_rent_point = ? AND id_vehicle = ?"
            );
            statement.setInt(1, model.getPreviousRentPointId());
            statement.setInt(2, model.getVehicleId());

            if (1 == statement.executeUpdate()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean setVehicleAvailableInNewRentalPoint(ReturnModel model) {
        try {
            PreparedStatement statement = DbConnection.getInstance().getConnection().prepareStatement(
                    "INSERT INTO car_rent.rental_point_vehicle (id_rent_point, id_vehicle, available) VALUE (?, ?, 1)"
            );
            statement.setInt(1, model.getCurrentRentPointId());
            statement.setInt(2, model.getVehicleId());
            if (1 == statement.executeUpdate()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean removeVehicleFromOriginRentalPoint(ReturnModel model) {
        try {
            PreparedStatement statement = DbConnection.getInstance().getConnection().prepareStatement(
                    "DELETE FROM car_rent.rental_point_vehicle WHERE id_rental_point = ? AND id_vehicle = ? AND available = 0"
            );
            statement.setInt(1, model.getPreviousRentPointId());
            statement.setInt(2, model.getVehicleId());
            if (1 == statement.executeUpdate()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
