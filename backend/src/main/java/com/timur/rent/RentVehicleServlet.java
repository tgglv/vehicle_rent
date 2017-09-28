package com.timur.rent;

import com.timur.rent.storage.DbConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RentVehicleServlet extends EnhancedServlet {

    private class Parameters {

        private Integer pointId;
        private Integer vehicleId;
        private Integer customerId;

        public Parameters(HttpServletRequest httpServletRequest) {
            pointId = parseInteger(httpServletRequest.getParameter("point_id"));
            vehicleId = parseInteger(httpServletRequest.getParameter("vehicle_id"));
            customerId = parseInteger(httpServletRequest.getParameter("customer_id"));
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

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        Parameters parameters = new Parameters(httpServletRequest);

        boolean isVehicleAvailable = false;
        boolean isCustomerExists = false;

        if (null != parameters.getPointId() && null != parameters.getVehicleId()) {
            try {
                PreparedStatement s1 = DbConnection.getInstance().getConnection().prepareStatement(
                        "SELECT COUNT(*) AS available FROM car_rent.rental_point_vehicle pv\n" +
                                "JOIN car_rent.vehicle v ON pv.id_vehicle = v.id\n" +
                                "JOIN car_rent.rental_point p ON pv.id_rent_point = p.id\n" +
                                "WHERE pv.id_rent_point = ? AND id_vehicle = ? AND available = ?"
                );
                s1.setInt(1, parameters.getPointId());
                s1.setInt(2, parameters.getVehicleId());
                s1.setInt(3, AvailableToRentVehicleListServlet.IS_AVAILABLE);
                ResultSet rs = s1.executeQuery();

                if (null == rs || !rs.next() || 1 != rs.getInt(1)) {
                    setResponse(httpServletResponse, 404, "{\"error\": \"Vehicle is not available\"}");
                    return ;
                }

                isVehicleAvailable = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (isVehicleAvailable) {
            if (null != parameters.getCustomerId()) {
                try {
                    PreparedStatement s2 = DbConnection.getInstance().getConnection().prepareStatement(
                            "SELECT count(*) AS was_found FROM car_rent.customer WHERE id = ?"
                    );
                    s2.setInt(1, parameters.getCustomerId());
                    ResultSet rs = s2.executeQuery();

                    if (null == rs || !rs.next() || 1 != rs.getInt(1)) {
                        setResponse(httpServletResponse, 404, "{\"error\": \"Customer is not exists\"}");
                        return ;
                    }

                    isCustomerExists = true;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        if (isCustomerExists) {
            try {
                DbConnection.getInstance().getConnection().setAutoCommit(false);

                PreparedStatement s3 = DbConnection.getInstance().getConnection().prepareStatement(
                        "UPDATE car_rent.rental_point_vehicle \n" +
                                "SET available = 0\n" +
                                "WHERE available = 1 AND id_rent_point = ? AND id_vehicle = ?"
                );
                s3.setInt(1, parameters.getPointId());
                s3.setInt(2, parameters.getVehicleId());

                int rowsUpdated = s3.executeUpdate();
                if (1 == rowsUpdated) { // поместили ТС в резерв

                    PreparedStatement s4 = DbConnection.getInstance().getConnection().prepareStatement(
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
                            DbConnection.getInstance().getConnection().commit();
                            DbConnection.getInstance().getConnection().setAutoCommit(true);

                            setResponse(httpServletResponse, 200, "{\"record_id\": " + recordId + "}");
                            return ;
                        }
                    }
                }

            } catch (SQLException e) {
                try {
                    DbConnection.getInstance().getConnection().rollback();
                } catch (SQLException e2) {
                    e2.printStackTrace();
                }
                e.printStackTrace();
            }
        }

        setSomethingWentWrongResponse(httpServletResponse);
    }
}
