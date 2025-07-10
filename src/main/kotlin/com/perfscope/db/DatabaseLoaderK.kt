package com.perfscope.db

import com.perfscope.model.CommandData
import com.perfscope.model.ThreadData
import com.perfscope.model2.tables.Comms

import com.perfscope.model2.tables.references.COMMS
import com.perfscope.model2.tables.references.COMM_THREADS
import com.perfscope.model2.tables.references.THREADS
import org.jooq.DSLContext
import java.sql.SQLException

class DatabaseLoaderK {

    @Throws(SQLException::class)
    fun fetchCommands(databasePath: String): List<CommandData> {
//        DatabaseLoader.logger.info("Loading comms with calls from: {}", databasePath)
        val result = mutableListOf<CommandData>()

        DatabaseConnectionHolderK(databasePath).use { connection: DatabaseConnectionHolderK ->
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
                    .map { (threadId, pid, tid) -> ThreadData(threadId, pid, tid) }
                val commandText = command.comm
                if (commandText != null) {
                    result += CommandData(command.id!!.toLong(), commandText, threads)
                }
            }

//            DatabaseLoader.logger.info("Found {} comms with calls", commsWithCalls.size)
        }
        return result
    }
}