package com.hei.prog3.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class TestDataSourceConfig {

    public static DataSource getDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/product_management_db");
        config.setUsername("product_manager_user");
        config.setPassword("ange");
        config.setMaximumPoolSize(5);
        return new HikariDataSource(config);
    }
}