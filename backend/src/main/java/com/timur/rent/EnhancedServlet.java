package com.timur.rent;

import com.mysql.cj.core.result.Field;
import com.mysql.cj.jdbc.result.ResultSetMetaData;
import com.timur.rent.model.VehicleType;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EnhancedServlet extends HttpServlet {
    protected Integer parseInteger(String s) {
        Integer result = null;
        if (null != s) {
            try {
                result = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    protected String parseVehicleType(String s) {
        if (null == s) {
            return null;
        }

        String[] availableTypes = new String[]{VehicleType.CAR, VehicleType.BIKE, VehicleType.SCOOTER};

        for (String type : availableTypes) {
            if (type.equals(s)) {
                return s;
            }
        }

        return null;
    }

    protected Float parseFloat(String s) {
        Float result = null;
        if (null != s) {
            try {
                result = Float.parseFloat(s);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    protected boolean searchByParam(String s) {
        return null != s && s.length() > 0;
    }

    protected boolean searchByParam(Integer i) {
        return null != i && i > 0;
    }

    protected String assertSqlDate(String s) {
        if (null == s) {
            return null;
        }
        Date date = null;
        try {
            String dateFormat = "yyyy-MM-dd";
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            date = formatter.parse(s);
            if (!s.equals(formatter.format(date))) {
                date = null;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (null == date) ? null : s;
    }

    protected String[] getColumnNames(ResultSet rs) {
        ResultSetMetaData metaData = null;
        String[] result = null;
        int columnCount = 0;

        try {
            metaData = (ResultSetMetaData) rs.getMetaData();
            if (null == metaData) {
                return null;
            }

            columnCount = metaData.getColumnCount();
            if (0 == columnCount) {
                return null;
            }

            Field[] fields = metaData.getFields();
            result = new String[fields.length];

            int i = 0;
            for (Field f : fields) {
                result[i++] = f.getColumnLabel();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    protected void setResponse(HttpServletResponse httpServletResponse, int status, String response) throws IOException {
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:9090");
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setStatus(status);
        httpServletResponse.getWriter().print(response);
    }

    protected void setSomethingWentWrongResponse(HttpServletResponse httpServletResponse) throws IOException {
        setResponse(httpServletResponse, 400, "{\"error\": \"Something went wrong\"}");
    }
}


