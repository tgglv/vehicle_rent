package com.timur.rent;

import com.google.gson.Gson;
import com.timur.rent.storage.DbConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class RentPointListServlet extends EnhancedServlet {

    public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        try {
            Statement statement = DbConnection.getInstance().getConnection().createStatement();
            ResultSet rs = statement.executeQuery(
                    "SELECT id, id_country, `name` FROM car_rent.rental_point ORDER by id_country, `name`"
            );

            if (null != rs) {
                Map<Integer, Map<Integer, String>> map = new HashMap<Integer, Map<Integer, String>>();
                while (rs.next()) {
                    int id = rs.getInt(1);
                    int countryId = rs.getInt(2);
                    String name = rs.getString(3);

                    if (!map.containsKey(countryId)) {
                        map.put(countryId, new HashMap<Integer, String>());
                    }

                    map.get(countryId).put(id, name);
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