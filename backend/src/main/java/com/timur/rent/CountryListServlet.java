package com.timur.rent;

import com.google.gson.Gson;
import com.timur.rent.storage.DbConnection;

import javax.servlet.http.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class CountryListServlet extends EnhancedServlet {

    public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {

        try {
            ResultSet rs;
            Map<Integer, String> map = new HashMap<Integer, String>();

            Statement statement = DbConnection.getInstance().getConnection().createStatement();
            rs = statement.executeQuery("SELECT id, name FROM country");

            if (null != rs) {
                while (rs.next()) {
                    map.put(rs.getInt("id"), rs.getString("name"));
                }
            }

            setResponse(httpServletResponse, 200, new Gson().toJson(map));
            return;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setResponse(httpServletResponse, 404, "{}");
    }
}