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

public class VehicleVendorListServler extends EnhancedServlet {

    public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        try {
            Statement statement = DbConnection.getInstance().getConnection().createStatement();
            ResultSet rs = statement.executeQuery("SELECT id, `name` FROM car_rent.vendor");
            Map<Integer, String> map = new HashMap<Integer, String>();
            if (null != rs) {
                while (rs.next()) {
                    map.put(rs.getInt("id"), rs.getString("name"));
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
