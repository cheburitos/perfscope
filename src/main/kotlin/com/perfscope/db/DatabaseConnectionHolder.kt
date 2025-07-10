package com.perfscope.db

import org.jooq.DSLContext
import org.jooq.ExecuteContext
import org.jooq.ExecuteListener
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.impl.DefaultConfiguration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DatabaseConnectionHolder(databasePath: String) : AutoCloseable {
    private val connection: Connection?
    val context: DSLContext

    init {
        logger.debug("Opening database connection to: $databasePath")
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath)

        val configuration = DefaultConfiguration()
        configuration.set(connection)
        configuration.set(SQLDialect.SQLITE)
        configuration.set(SqlQueryLoggingListener())

        this.context = DSL.using(configuration)
    }

    @Throws(SQLException::class)
    override fun close() {
        if (connection != null && !connection.isClosed) {
            connection.close()
        }
    }

    private class SqlQueryLoggingListener : ExecuteListener {
        private var startTime: Long = 0

        override fun executeStart(ctx: ExecuteContext) {
            startTime = System.nanoTime()
            if (logger.isDebugEnabled) {
                logger.debug("Executing SQL: {}", ctx.sql())
            }
        }

        override fun executeEnd(ctx: ExecuteContext?) {
            if (logger.isDebugEnabled) {
                val elapsedTime: Long = System.nanoTime() - startTime
                logger.debug("SQL execution completed in {} ms", elapsedTime / 1000000.0)
            }
        }

        override fun exception(ctx: ExecuteContext) {
            logger.error("SQL error: {}", ctx.exception()!!.message, ctx.exception())
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DatabaseConnectionHolder::class.java)
    }
}