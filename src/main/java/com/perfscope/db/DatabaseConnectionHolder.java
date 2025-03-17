package com.perfscope.db;

import org.jooq.DSLContext;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionHolder implements AutoCloseable {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionHolder.class);
    private final Connection connection;
    private final DSLContext context;
    
    public DatabaseConnectionHolder(String databasePath) throws SQLException {
        logger.debug("Opening database connection to: {}", databasePath);
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
        
        // Create a configuration with the SQL query logging listener
        DefaultConfiguration configuration = new DefaultConfiguration();
        configuration.set(connection);
        configuration.set(SQLDialect.SQLITE);
        configuration.set(new SqlQueryLoggingListener());
        
        this.context = DSL.using(configuration);
    }
    
    public DSLContext getContext() {
        return context;
    }
    
    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
    
    /**
     * Custom execution listener that logs SQL queries and their execution times
     */
    private static class SqlQueryLoggingListener implements ExecuteListener {
        private long startTime;
        
        @Override
        public void executeStart(ExecuteContext ctx) {
            startTime = System.nanoTime();
            if (logger.isDebugEnabled()) {
                logger.debug("Executing SQL: {}", ctx.sql());
            }
        }
        
        @Override
        public void executeEnd(ExecuteContext ctx) {
            if (logger.isDebugEnabled()) {
                long elapsedTime = System.nanoTime() - startTime;
                logger.debug("SQL execution completed in {} ms", elapsedTime / 1_000_000.0);
            }
        }
        
        @Override
        public void exception(ExecuteContext ctx) {
            logger.error("SQL error: {}", ctx.exception().getMessage(), ctx.exception());
        }

        // Required empty implementations for other ExecuteListener methods
        @Override public void start(ExecuteContext ctx) {}
        @Override public void renderStart(ExecuteContext ctx) {}
        @Override public void renderEnd(ExecuteContext ctx) {}
        @Override public void prepareStart(ExecuteContext ctx) {}
        @Override public void prepareEnd(ExecuteContext ctx) {}
        @Override public void bindStart(ExecuteContext ctx) {}
        @Override public void bindEnd(ExecuteContext ctx) {}
        @Override public void outStart(ExecuteContext ctx) {}
        @Override public void outEnd(ExecuteContext ctx) {}
        @Override public void fetchStart(ExecuteContext ctx) {}
        @Override public void resultStart(ExecuteContext ctx) {}
        @Override public void recordStart(ExecuteContext ctx) {}
        @Override public void recordEnd(ExecuteContext ctx) {}
        @Override public void resultEnd(ExecuteContext ctx) {}
        @Override public void fetchEnd(ExecuteContext ctx) {}
        @Override public void end(ExecuteContext ctx) {}
        @Override public void warning(ExecuteContext ctx) {}
    }
} 