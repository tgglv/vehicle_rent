package com.timur.rent;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

public class VehicleTypeListServlet extends EnhancedServlet {

    public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        map.put("car", "Автомобиль");
        map.put("bike", "Мотоцикл");
        map.put("scooter", "Скутер");
        setResponse(httpServletResponse, 200, new Gson().toJson(map));
    }

}
