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

public class VehicleVendorsResource extends ServerResource {

    @Get("json")
    public String getVendors() {
        try {
            Statement statement = Connection.getInstance().getConnection().createStatement();
            ResultSet rs = statement.executeQuery("SELECT id, `name` FROM car_rent.vendor");
            Map<Integer, String> map = new HashMap<Integer, String>();
            if (null != rs) {
                while (rs.next()) {
                    map.put(rs.getInt("id"), rs.getString("name"));
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
