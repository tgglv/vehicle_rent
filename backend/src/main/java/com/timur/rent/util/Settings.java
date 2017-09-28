package com.timur.rent.util;

public class Settings {

    private static Settings instance = null;

    private static final String MODE = "DEV";

    private String mysqlUri;
    private String mysqlUsername;
    private String mysqlPassword;

    protected Settings() {
        if (MODE.equals("DEV")) {
            mysqlUri = "jdbc:mysql://t-gogolev.user:3306/car_rent?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&characterEncoding=UTF-8";
            mysqlUsername = "root";
            mysqlPassword = "root";
        } else {
            mysqlUri = "jdbc:mysql://localhost:3306/car_rent?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&characterEncoding=UTF-8";
            mysqlUsername = "dev";
            mysqlPassword = "dev";
        }
    }

    public static Settings getInstance() {
        if (null == instance) {
            instance = new Settings();
        }
        return instance;
    }

    public String getMysqlUri() {
        return mysqlUri;
    }

    public String getMysqlUsername() {
        return mysqlUsername;
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }
}
