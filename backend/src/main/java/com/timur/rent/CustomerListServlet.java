package com.timur.rent;

import com.google.gson.Gson;
import com.timur.rent.storage.DbConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class CustomerListServlet extends EnhancedServlet {

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        String customerName = httpServletRequest.getParameter("customer");
        Integer countryId = parseInteger(httpServletRequest.getParameter("country"));

        String sqlQuery = "SELECT customer.id AS customer_id,\n" +
                "customer.name AS customer_name,\n" +
                "country.name AS country_name\n" +
                "FROM car_rent.customer customer\n" +
                "JOIN car_rent.country country ON customer.id_country = country.id\n" +
                "WHERE 1\n";

        if (searchByParam(countryId)) {
            sqlQuery += "AND customer.id_country = ?\n";
        }
        if (searchByParam(customerName)) {
            try {
                customerName = URLDecoder.decode(customerName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (0 == customerName.trim().length()) {
                customerName = null;
            } else {
                sqlQuery +=  "AND customer.`name` LIKE CONCAT('%', ?, '%')\n";
            }
        }

        int index = 0;
        try {
            PreparedStatement statement = DbConnection.getInstance().getConnection().prepareStatement(sqlQuery);
            if (searchByParam(countryId)) {
                statement.setInt(++index, countryId);
            }
            if (searchByParam(customerName)) {
                statement.setString(++index, customerName);
            }
            ResultSet rs = statement.executeQuery();
            String[] customerFields = new String[]{"customer_name", "country_name"};
            if (null != rs) {
                HashMap<Integer, HashMap<String, String>> map = new HashMap<Integer, HashMap<String, String>>();
                while (rs.next()) {
                    HashMap<String, String> customerInfo = new HashMap<String, String>();
                    for (String field : customerFields) {
                        customerInfo.put(field, rs.getString(field));
                    }
                    map.put(rs.getInt("customer_id"), customerInfo);
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
