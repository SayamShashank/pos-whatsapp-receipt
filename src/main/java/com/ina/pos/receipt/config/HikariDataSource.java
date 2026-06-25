package com.ina.pos.receipt.config;

import com.zaxxer.hikari.HikariConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class HikariDataSource {

    private final DBProperties dbProperties;
    
    public HikariDataSource(DBProperties dbProperties) {
        this.dbProperties = dbProperties;
    }

    @Bean
    public DataSource dataSource() {

        HikariConfig config = createHikariConfig();

        config.addDataSourceProperty("cachePrepStmts", dbProperties.isCachePrepStmts());
        config.addDataSourceProperty("prepStmtCacheSize", dbProperties.getPrepStmtCacheSize());
        config.addDataSourceProperty("prepStmtCacheSqlLimit", dbProperties.getPrepStmtCacheSqlLimit());
        config.addDataSourceProperty("useServerPrepStmts", dbProperties.isUseServerPrepStmts());
        config.addDataSourceProperty("housekeepingPeriodMs", dbProperties.getHousekeepingPeriodMs());

        return new com.zaxxer.hikari.HikariDataSource(config);
    }

    private HikariConfig createHikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbProperties.getUrl());
        config.setUsername(dbProperties.getUsername());
        config.setPassword(dbProperties.getPassword());
        config.setDriverClassName(dbProperties.getDriverClassName());
        config.setPoolName(dbProperties.getPoolName());
        config.setMaximumPoolSize(dbProperties.getMaxPoolSize());
        config.setMinimumIdle(dbProperties.getMinimumIdle());
        config.setIdleTimeout(dbProperties.getIdleTimeout());
        config.setMaxLifetime(dbProperties.getMaxLifetime());
        config.setConnectionTimeout(dbProperties.getConnectionTimeout());
        config.setLeakDetectionThreshold(dbProperties.getLeakDetectionThreshold());
        config.setAutoCommit(dbProperties.isAutoCommit());
        return config;
    }
}
