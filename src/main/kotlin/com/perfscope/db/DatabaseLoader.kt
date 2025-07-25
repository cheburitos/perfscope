package com.perfscope.db

import com.perfscope.model.Call
import com.perfscope.model.Command
import com.perfscope.model.Thread
import com.perfscope.model.tables.Comms
import com.perfscope.model.tables.references.CALLS
import com.perfscope.model.tables.references.CALL_PATHS
import com.perfscope.model.tables.references.COMMS
import com.perfscope.model.tables.references.COMM_THREADS
import com.perfscope.model.tables.references.DSOS
import com.perfscope.model.tables.references.SYMBOLS
import com.perfscope.model.tables.references.THREADS
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.SQLException
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class DatabaseLoader(private val dbPath: String) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(DatabaseLoader::class.java)
        const val ROOT_PARENT_CALL_PATH_ID: Long = 1L
    }

    @Throws(SQLException::class)
    fun fetchCommands(dbPath: String): List<Command> {
        val result = mutableListOf<Command>()

        DatabaseConnectionHolder(dbPath).use { connection: DatabaseConnectionHolder ->
            val context: DSLContext = connection.context
            val commandsWithCalls = context.selectFrom(Comms.COMMS)
                .where(COMMS.HAS_CALLS.eq(true))
                .fetch()

            commandsWithCalls.forEach { command ->
                val threads = context.select<Long?, Int?, Int?>(
                    COMM_THREADS.THREAD_ID,
                    THREADS.PID,
                    THREADS.TID)
                    .from(COMM_THREADS)
                    .innerJoin(THREADS).on(COMM_THREADS.THREAD_ID.eq(THREADS.ID.cast(Long::class.java)))
                    .where(COMM_THREADS.COMM_ID.eq(command.id?.toLong()))
                    .and(COMM_THREADS.THREAD_ID.isNotNull)
                    .fetch()
                    .map { (threadId, pid, tid) -> Thread(threadId, pid, tid) }
                val commandText = command.comm
                if (commandText != null) {
                    result += Command(command.id!!.toLong(), commandText, threads)
                }
            }

            logger.info("Found ${commandsWithCalls.size} commands with calls")
        }
        return result
    }

    fun fetchCalls(commandId: Long, threadId: Long, parentCallPathId: Long, fromCallTime: Long?, toReturnTime: Long?): List<Call> {
        DatabaseConnectionHolder(dbPath).use { connection: DatabaseConnectionHolder ->
            var select = connection.context.select(
                CALLS.CALL_PATH_ID,
                SYMBOLS.NAME,
                CALLS.CALL_TIME,
                CALLS.RETURN_TIME,
                CALLS.RETURN_TIME - CALLS.CALL_TIME)
                .from(CALLS)
                .innerJoin(CALL_PATHS).on(CALLS.CALL_PATH_ID.eq(CALL_PATHS.ID.cast(Long::class.java)))
                .innerJoin(SYMBOLS).on(CALL_PATHS.SYMBOL_ID.eq(SYMBOLS.ID.cast(Long::class.java)))
                .innerJoin(DSOS).on(SYMBOLS.DSO_ID.eq(DSOS.ID.cast(Long::class.java)))
                .where(CALLS.PARENT_CALL_PATH_ID.eq(parentCallPathId))
                .and(CALLS.COMM_ID.eq(commandId))
                .and(CALLS.THREAD_ID.eq(threadId))
            if (fromCallTime != null && toReturnTime != null) {
                select = select
                    .and(CALLS.CALL_TIME.greaterOrEqual(fromCallTime))
                    .and(CALLS.RETURN_TIME.lessOrEqual(toReturnTime))
            }

            val result = select.orderBy(CALLS.CALL_TIME, CALLS.CALL_PATH_ID)
                .fetch()
                .map { (callPathId, name, callTime, returnTime, duration) ->
                    Call(name, duration, callPathId, duration, callTime, returnTime)
                }
            return result
        }
    }

    fun search(commandId: Long, threadId: Long, searchTerm: String) : Boolean {
        DatabaseConnectionHolder(dbPath).use { connection: DatabaseConnectionHolder ->
            return connection.context.select(THREADS.TID)
                .from(CALLS)
                .innerJoin(THREADS).on(CALLS.THREAD_ID.eq(THREADS.ID.cast(Long::class.java)))
                .innerJoin(CALL_PATHS).on(CALLS.CALL_PATH_ID.eq(CALL_PATHS.ID.cast(Long::class.java)))
                .innerJoin(SYMBOLS).on(CALL_PATHS.SYMBOL_ID.eq(SYMBOLS.ID.cast(Long::class.java)))
                .where(CALLS.COMM_ID.eq(commandId))
                .and(CALLS.THREAD_ID.eq(threadId))
                .and(SYMBOLS.NAME.like("%${searchTerm}%"))
                .limit(1)
                .fetch()
                .map { (id) -> id }
                .isEmpty()
                .not()
        }
    }

    fun calcTotalTime(commandId: Long, threadId: Long): Duration {
        DatabaseConnectionHolder(dbPath).use { connection: DatabaseConnectionHolder ->
            val totalDurationNanos = connection.context.select(DSL.sum(CALLS.RETURN_TIME - CALLS.CALL_TIME).cast(Long::class.java))
                .from(CALLS)
                .where(CALLS.PARENT_CALL_PATH_ID.eq(ROOT_PARENT_CALL_PATH_ID))
                .and(CALLS.COMM_ID.eq(commandId))
                .and(CALLS.THREAD_ID.eq(threadId))
                .groupBy(CALLS.CALL_PATH_ID)
                .fetch()
                .map { (value) -> value }
                .sumOf { it }
            return totalDurationNanos.toDuration(DurationUnit.NANOSECONDS)
        }
    }
}