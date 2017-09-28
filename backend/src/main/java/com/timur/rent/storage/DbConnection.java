package com.timur.rent.storage;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.SQLException;

public class DbConnection {

    private static DbConnection instance = null;

    private java.sql.Connection connection = null;

    protected DbConnection() throws SQLException {
        // TODO: брать URL из конфигурации
        String url = "jdbc:mysql://localhost:3306/car_rent?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&characterEncoding=UTF-8";
        String username = "dev";
        String password = "dev";

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser(username);
        dataSource.setPassword(password);
        dataSource.setUrl(url);

        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static DbConnection getInstance() throws SQLException {
        if (null == instance) {
            instance = new DbConnection();
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
