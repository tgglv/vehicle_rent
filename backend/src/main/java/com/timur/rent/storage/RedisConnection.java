package com.timur.rent.storage;

import redis.clients.jedis.Jedis;

public class RedisConnection {

    private static RedisConnection instance = null;

    private Jedis connection = null;

    protected RedisConnection() {
        // TODO: брать host и port из конфигурации
        connection = new Jedis("localhost", 6379);
    }

    public static RedisConnection getInstance() {
        if (null == instance) {
            instance = new RedisConnection();
        }
        return instance;
    }

    public Jedis getConnection() {
        return connection;
    }

    public void closeConnection() {
        connection.close();
    }
}
