package com.timur.rent.resource;

import com.google.gson.Gson;
import com.timur.rent.util.Connection;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class CountryResource extends ServerResource {

    @Get("json")
    public String json() {

        try {
            ResultSet rs;
            Map<Integer, String> map = new HashMap<Integer, String>();

            Statement statement = Connection.getInstance().getConnection().createStatement();
            rs = statement.executeQuery("SELECT id, name FROM country");

            if (null != rs) {
                while (rs.next()) {
                    map.put(rs.getInt(1), rs.getString(2));
                }
            }

            return new Gson().toJson(map);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getResponse().setStatus(new Status(404));
        return "{}";
    }

}
