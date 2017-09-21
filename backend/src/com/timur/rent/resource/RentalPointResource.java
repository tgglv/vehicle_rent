package com.timur.rent.resource;

import com.google.gson.Gson;
import com.timur.rent.util.Connection;
import org.restlet.Request;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class RentalPointResource extends ServerResource {

    @Get("json")
    public String fetchAllRentalPoints() {
        String json = "";
        try {
            ResultSet rs;

            Statement statement = Connection.getInstance().getConnection().createStatement();
            rs = statement.executeQuery(
                    "SELECT id, id_country, `name` " +
                            "FROM car_rent.rental_point " +
                            "ORDER by id_country, `name`"
            );

            Map<Integer, Map<Integer, String>> map = new HashMap<Integer, Map<Integer, String>>();

            if (null != rs) {
                while (rs.next()) {
                    int id = rs.getInt(1);
                    int countryId = rs.getInt(2);
                    String name = rs.getString(3);

                    if (!map.containsKey(countryId)) {
                        map.put(countryId, new HashMap<Integer, String>());
                    }

                    map.get(countryId).put(id, name);
                }
            }
            json = new Gson().toJson(map);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return json;
    }
}
