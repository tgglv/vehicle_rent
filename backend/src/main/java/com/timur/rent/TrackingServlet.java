package com.timur.rent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.timur.rent.storage.RedisConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

public class TrackingServlet extends EnhancedServlet {

    public static final String POINT_LATITUDE = "latitude";
    public static final String POINT_LONGITUDE = "longitude";

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        Integer vehicleId = getVehicleId(httpServletRequest);
        Gson gson = new Gson();
        if (!searchByParam(vehicleId)) {
            setResponse(httpServletResponse, 403, "{\"error\": \"Vehicle ID was not specified\"}");
            return;
        }

        String location = RedisConnection.getInstance().getConnection().get(getRedisKey(vehicleId));
        if (null == location) {
            setResponse(httpServletResponse, 404, "{\"error\": \"Vehicle location was not specified\"}");
            return;
        }

        Type pointType = new TypeToken<HashMap<String, Float>>() {
        }.getType();
        HashMap<String, Float> point = gson.fromJson(location, pointType);

        if (!point.containsKey(POINT_LATITUDE) || !point.containsKey(POINT_LONGITUDE)) {
            setSomethingWentWrongResponse(httpServletResponse);
            return;
        }

        setResponse(httpServletResponse, 200, getLocationJson(point.get(POINT_LATITUDE), point.get(POINT_LONGITUDE)));
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        Integer vehicleId = getVehicleId(httpServletRequest);
        if (!searchByParam(vehicleId)) {
            setResponse(httpServletResponse, 403, "{\"error\": \"Vehicle ID was not specified\"}");
            return;
        }
        Float latitude = parseFloat(httpServletRequest.getParameter(POINT_LATITUDE));
        Float longitude = parseFloat(httpServletRequest.getParameter(POINT_LONGITUDE));

        if (null == latitude || null == longitude) {
            setResponse(httpServletResponse, 403, "{\"error\": \"Latitude and/or Longitude was not specified\"}");
            return;
        }

        RedisConnection.getInstance().getConnection().set(getRedisKey(vehicleId), getLocationJson(latitude, longitude));

        setResponse(httpServletResponse, 204, "");
    }

    private Integer getVehicleId(HttpServletRequest httpServletRequest) {
        return parseInteger(httpServletRequest.getParameter("vehicle_id"));
    }

    private String getRedisKey(int vehicleId) {
        return "vehicle:id:" + vehicleId;
    }

    private String getLocationJson(float latitude, float longitude) {
        HashMap<String, Float> map = new HashMap<String, Float>();
        map.put(POINT_LATITUDE, latitude);
        map.put(POINT_LONGITUDE, longitude);
        return new Gson().toJson(map);
    }

}
