package com.perfscope.db;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
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
        this.context = DSL.using(connection, SQLDialect.SQLITE);
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
} 