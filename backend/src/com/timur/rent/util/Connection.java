package com.timur.rent.util;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.SQLException;

public class Connection {
    private static Connection instance = null;

    private java.sql.Connection connection = null;

    protected Connection() {
        // TODO: брать URL из конфигурации
        String url = "jdbc:mysql://t-gogolev.user:3306/car_rent?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&characterEncoding=UTF-8";
        String username = "root";
        String password = "root";

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser(username);
        dataSource.setPassword(password);
        dataSource.setUrl(url);

        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // TODO: есть создать соединение не удалось - выбросить исключение
    }

    public static Connection getInstance() {
        if (null == instance) {
            instance = new Connection();
        }
        return instance;
    }

    public java.sql.Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
