package com.ina.pos.receipt.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "ina.database")
@Component
@Getter
@Setter
public class DBProperties {

    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private String poolName;
    private long idleTimeout;
    private int maxPoolSize;
    private long leakDetectionThreshold;
    private long connectionTimeout;
    private int minimumIdle;
    private long maxLifetime;
    private boolean cachePrepStmts;
    private int prepStmtCacheSize;
    private int prepStmtCacheSqlLimit;
    private boolean useServerPrepStmts;
    private long housekeepingPeriodMs;
    private boolean autoCommit;
}
