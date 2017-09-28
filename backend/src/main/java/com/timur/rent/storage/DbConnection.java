package com.timur.rent.storage;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.timur.rent.util.Settings;

import java.sql.SQLException;

public class DbConnection {

    private static DbConnection instance = null;

    private java.sql.Connection connection = null;

    protected DbConnection() throws SQLException {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl(Settings.getInstance().getMysqlUri());
        dataSource.setUser(Settings.getInstance().getMysqlUsername());
        dataSource.setPassword(Settings.getInstance().getMysqlPassword());

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
