package com.timur.rent.resource;

import com.timur.rent.util.Connection;
import org.restlet.data.Status;
import org.restlet.resource.Delete;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RentalPointRentResource extends ServerResource {

    private class Parameters {

        private Integer pointId;
        private Integer vehicleId;
        private Integer customerId;

        public Parameters() {
            String pointId = (String) getRequest().getAttributes().get("point_id");
            String vehicleId = (String) getRequest().getAttributes().get("vehicle_id");
            String customerId = (String) getRequest().getAttributes().get("customer_id");

            this.pointId = parse(pointId);
            this.vehicleId = parse(vehicleId);
            this.customerId = parse(customerId);
        }

        private Integer parse(String input) {
            Integer result = null;
            try {
                result = (null == input)
                        ? null
                        : Integer.parseInt(input);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return result;
        }

        public Integer getPointId() {
            return this.pointId;
        }

        public Integer getVehicleId() {
            return vehicleId;
        }

        public Integer getCustomerId() {
            return customerId;
        }
    }

    @Post("json")
    public String rentVehicle() {
        Parameters parameters = new Parameters();

        boolean isVehicleAvailable = false;
        boolean isCustomerExists = false;

        if (null != parameters.getPointId() && null != parameters.getVehicleId()) {
            try {
                PreparedStatement s1 = Connection.getInstance().getConnection().prepareStatement(
                        "SELECT COUNT(*) AS available FROM car_rent.rental_point_vehicle pv\n" +
                                "JOIN car_rent.vehicle v ON pv.id_vehicle = v.id\n" +
                                "JOIN car_rent.rental_point p ON pv.id_rent_point = p.id\n" +
                                "WHERE pv.id_rent_point = ? AND id_vehicle = ? AND available = ?"
                );
                s1.setInt(1, parameters.getPointId());
                s1.setInt(2, parameters.getVehicleId());
                s1.setInt(3, RentalPointVehicleListResource.IS_AVAILABLE);
                ResultSet rs = s1.executeQuery();

                if (null == rs || !rs.next() || 1 != rs.getInt(1)) {
                    getResponse().setStatus(new Status(404));
                    return "{\"error\": \"Vehicle is not available\"}";
                }

                isVehicleAvailable = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (isVehicleAvailable) {
            if (null != parameters.getCustomerId()) {
                try {
                    PreparedStatement s2 = Connection.getInstance().getConnection().prepareStatement(
                            "SELECT count(*) AS was_found FROM car_rent.customer WHERE id = ?"
                    );
                    s2.setInt(1, parameters.getCustomerId());
                    ResultSet rs = s2.executeQuery();

                    if (null == rs || !rs.next() || 1 != rs.getInt(1)) {
                        getResponse().setStatus(new Status(404));
                        return "{\"error\": \"Customer is not exists\"}";
                    }

                    isCustomerExists = true;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        if (isCustomerExists) {
            try {
                Connection.getInstance().getConnection().setAutoCommit(false);

                PreparedStatement s3 = Connection.getInstance().getConnection().prepareStatement(
                        "UPDATE car_rent.rental_point_vehicle \n" +
                                "SET available = 0\n" +
                                "WHERE available = 1 AND id_rent_point = ? AND id_vehicle = ?"
                );
                s3.setInt(1, parameters.getPointId());
                s3.setInt(2, parameters.getVehicleId());

                int rowsUpdated = s3.executeUpdate();
                if (1 == rowsUpdated) { // поместили ТС в резерв

                    PreparedStatement s4 = Connection.getInstance().getConnection().prepareStatement(
                            "INSERT INTO car_rent.vehicle_rent (id_vehicle, id_customer, id_rent_point) VALUES (?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    s4.setInt(1, parameters.getVehicleId());
                    s4.setInt(2, parameters.getCustomerId());
                    s4.setInt(3, parameters.getPointId());

                    int rowsInserted = s4.executeUpdate();
                    if (1 == rowsInserted) {
                        ResultSet rs = s4.getGeneratedKeys();
                        if (rs.next()) {
                            int recordId = rs.getInt(1);
                            Connection.getInstance().getConnection().commit();
                            Connection.getInstance().getConnection().setAutoCommit(true);
                            getResponse().setStatus(new Status(200));
                            return "{\"record_id\": " + recordId + "}";
                        }
                    }
                }

            } catch (SQLException e) {
                try {
                    Connection.getInstance().getConnection().rollback();
                } catch (SQLException e2) {
                    e2.printStackTrace();
                }
                e.printStackTrace();
            }
        }

        getResponse().setStatus(new Status(400));
        return "{\"error\": \"Something went wrong\"}";
    }

}
