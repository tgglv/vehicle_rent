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
import java.util.ArrayList;
import java.util.HashMap;

public class StatisticsServlet extends EnhancedServlet {

    public final int STATISTICS_BY_VEHICLE_TYPE = 0;
    public final int STATISTICS_BY_VEHICLE_TYPE_AND_VENDOR = 1;

    abstract class BaseSqlExecutor {

        public Object getResult(Integer countryId, Integer pointId) {
            String sqlQuery = getQuery(countryId, pointId);

            int index = 0;
            try {
                PreparedStatement statement = DbConnection.getInstance().getConnection().prepareStatement(sqlQuery);
                if (searchByParam(countryId)) {
                    statement.setInt(++index, countryId);
                }
                if (searchByParam(pointId)) {
                    statement.setInt(++index, pointId);
                }
                ResultSet rs = statement.executeQuery();
                if (null != rs) {
                    while (rs.next()) {
                        processRow(rs);
                    }
                    return getData();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected String toHumanReadable(float minutes) {
            int minutesRemained = Math.round(minutes);
            int[] divisors = new int[]{365 * 24 * 60, 30 * 24 * 60, 24 * 60, 60};
            String[] captions = new String[]{"y", "M", "d", "h"};

            String result = "";
            for (int i = 0; i < divisors.length; ++i) {
                if (minutesRemained < divisors[i]) {
                    continue;
                }
                int quotient = minutesRemained / divisors[i];
                minutesRemained = minutesRemained % divisors[i];
                if (quotient > 0) {
                    result += " " + quotient + captions[i];
                }
            }
            if (minutesRemained > 0) {
                result += " " + minutesRemained + "m";
            }

            return result.trim();
        }

        abstract protected String getQuery(Integer countryId, Integer pointId);

        abstract protected void processRow(ResultSet rs);

        abstract protected Object getData();

        abstract public int getIndex();
    }

    class StatisticsByVehicleType extends BaseSqlExecutor {

        private HashMap<String, String> map = new HashMap<String, String>();

        public int getIndex() {
            return STATISTICS_BY_VEHICLE_TYPE;
        }

        protected String getQuery(Integer countryId, Integer pointId) {
            String sqlQuery = "SELECT vehicle.type,\n" +
                    "@days_diff := DATEDIFF(return_time, rent_time) AS days_diff,\n" +
                    "@time_diff_date := DATE_ADD(rent_time, INTERVAL DATEDIFF(return_time, rent_time) DAY) AS time_diff_date,\n" +
                    "@days_diff * 24 * 60 +\n" +
                    "(\n" +
                    "  IF ('-' = MID(TIMEDIFF(return_time, @time_diff_date), 1, 1), -1, 1) \n" +
                    "  * ( \n" +
                    "    HOUR(TIMEDIFF(return_time, @time_diff_date)) * 60 \n" +
                    "    + MINUTE(TIMEDIFF(return_time, @time_diff_date)) \n" +
                    "  )\n" +
                    ") AS avg_minutes\n" +
                    "FROM car_rent.vehicle_rent vr\n" +
                    "JOIN car_rent.vehicle vehicle ON vr.id_vehicle = vehicle.id\n" +
                    "JOIN car_rent.rental_point p ON p.id = vr.id_rent_point\n" +
                    "WHERE\n";

            if (searchByParam(countryId)) {
                sqlQuery += "  p.id_country = ? AND\n";
            }
            if (searchByParam(pointId)) {
                sqlQuery += "  id_rent_point = ? AND\n";
            }

            sqlQuery += "  id_return_point IS NOT NULL\n" +
                    "GROUP BY vehicle.type";

            return sqlQuery;
        }

        protected void processRow(ResultSet rs) {
            try {
                map.put(rs.getString("type"), toHumanReadable(rs.getFloat("avg_minutes")));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        protected Object getData() {
            return map;
        }
    }

    class StatisticsByVehicleTypeAndVendor extends BaseSqlExecutor {

        private HashMap<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>();

        public int getIndex() {
            return STATISTICS_BY_VEHICLE_TYPE_AND_VENDOR;
        }

        protected String getQuery(Integer countryId, Integer pointId) {

            String sqlQuery = "SELECT vehicle.type, vendor.name AS vendor_name,\n" +
                    "@days_diff := DATEDIFF(return_time, rent_time) AS days_diff,\n" +
                    "@time_diff_date := DATE_ADD(rent_time, INTERVAL DATEDIFF(return_time, rent_time) DAY) AS time_diff_date,\n" +
                    "@days_diff * 24 * 60 +\n" +
                    "(\n" +
                    "  IF ('-' = MID(TIMEDIFF(return_time, @time_diff_date), 1, 1), -1, 1) \n" +
                    "  * ( \n" +
                    "    HOUR(TIMEDIFF(return_time, @time_diff_date)) * 60 \n" +
                    "    + MINUTE(TIMEDIFF(return_time, @time_diff_date)) \n" +
                    "  )\n" +
                    ") AS avg_minutes\n" +
                    "FROM car_rent.vehicle_rent vr\n" +
                    "JOIN car_rent.vehicle vehicle ON vr.id_vehicle = vehicle.id\n" +
                    "JOIN car_rent.vendor vendor ON vendor.id = vehicle.id_vendor\n" +
                    "JOIN car_rent.rental_point p ON p.id = vr.id_rent_point\n" +
                    "WHERE\n";

            if (searchByParam(countryId)) {
                sqlQuery += "  p.id_country = ? AND\n";
            }
            if (searchByParam(pointId)) {
                sqlQuery += "  id_rent_point = ? AND\n";
            }

            sqlQuery += "  id_return_point IS NOT NULL\n" +
                    "GROUP BY vehicle.type, vehicle.id_vendor";

            return sqlQuery;
        }

        protected void processRow(ResultSet rs) {
            try {
                String vehicleType = rs.getString("type");
                String vendorName = rs.getString("vendor_name");
                String value = toHumanReadable(rs.getFloat("avg_minutes"));
                if (!map.containsKey(rs.getString("type"))) {
                    HashMap<String, String> innerMap = new HashMap<String, String>();
                    innerMap.put(vendorName, value);
                    map.put(vehicleType, innerMap);
                } else {
                    map.get(vehicleType).put(vendorName, value);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        protected Object getData() {
            return map;
        }
    }

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        Integer countryId = parseInteger(httpServletRequest.getParameter("country_id"));
        Integer pointId = parseInteger(httpServletRequest.getParameter("point_id"));

        ArrayList<Object> output = new ArrayList<Object>();

        BaseSqlExecutor[] requestList = new BaseSqlExecutor[]{
                new StatisticsByVehicleType(),
                new StatisticsByVehicleTypeAndVendor()
        };

        for (BaseSqlExecutor ex : requestList) {
            output.add(ex.getIndex(), ex.getResult(countryId, pointId));
        }

        setResponse(httpServletResponse, 200, new Gson().toJson(output));
    }
}
