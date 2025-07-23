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

class DatabaseConnectionHolder(dbPath: String) : AutoCloseable {
    private val connection: Connection
    val context: DSLContext

    init {
        logger.debug("Opening database connection to: $dbPath")
        this.connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")

        val configuration = DefaultConfiguration()
        configuration.set(connection)
        configuration.set(SQLDialect.SQLITE)
        configuration.set(SqlQueryLoggingListener())

        this.context = DSL.using(configuration)
    }

    @Throws(SQLException::class)
    override fun close() {
        if (!connection.isClosed) {
            connection.close()
        }
    }

    private class SqlQueryLoggingListener : ExecuteListener {
        private var startTime: Long = 0

        override fun executeStart(ctx: ExecuteContext) {
            startTime = System.nanoTime()
            logger.info("Executing SQL: ${ctx.sql()}")
        }

        override fun executeEnd(ctx: ExecuteContext?) {
            val elapsedTime: Long = System.nanoTime() - startTime
            logger.info("SQL execution completed in ${elapsedTime / 1000000.0} ms")
        }

        override fun exception(ctx: ExecuteContext) {
            logger.error("SQL error: {}", ctx.exception()!!.message, ctx.exception())
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DatabaseConnectionHolder::class.java)
    }
}