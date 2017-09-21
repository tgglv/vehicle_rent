package com.timur.rent.resource;

import com.google.gson.Gson;
import com.timur.rent.util.Connection;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomerList extends ServerResource {

    @Get("json")
    public String getCustomerList() {
        String customerName = getQuery().getValues("customer");
        String country = getQuery().getValues("country");

        String sqlQuery = "SELECT customer.id AS customer_id,\n" +
                "customer.name AS customer_name,\n" +
                "country.name AS country_name\n" +
                "FROM car_rent.customer customer\n" +
                "JOIN car_rent.country country ON customer.id_country = country.id\n" +
                "WHERE 1\n";

        Integer countryId = null;
        if (null != country) {
            try {
                countryId = Integer.parseInt(country);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (-1 == countryId) {
                countryId = null;
            }
        }

        if (null != countryId) {
            sqlQuery += "AND customer.id_country = ?\n";
        }
        if (null != customerName) {
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
            PreparedStatement statement = Connection.getInstance().getConnection().prepareStatement(sqlQuery);
            if (null != countryId) {
                statement.setInt(++index, countryId);
            }
            if (null != customerName) {
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
