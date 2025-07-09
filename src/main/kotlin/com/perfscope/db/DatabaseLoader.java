package com.perfscope.db;

import com.perfscope.model.CallTreeData;
import com.perfscope.model.CommandData;
import com.perfscope.model.tables.Calls;
import com.perfscope.model.tables.CommThreads;
import com.perfscope.model.tables.Comms;
import com.perfscope.model.tables.Threads;
import com.perfscope.model.tables.records.CommsRecord;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseLoader.class);
    private static final long ROOT_PARENT_CALL_PATH_ID = 1L;

    public List<CommandData> loadCommands(String databasePath) throws SQLException {
        logger.info("Loading comms with calls from: {}", databasePath);
        List<CommandData> result = new ArrayList<>();
        
        try (DatabaseConnectionHolder dbConnection = new DatabaseConnectionHolder(databasePath)) {
            DSLContext context = dbConnection.getContext();
            
            List<CommsRecord> commsWithCalls = context
                .selectFrom(Comms.COMMS)
                .where(Comms.COMMS.HAS_CALLS.eq(true))
                .fetch();
            
            logger.info("Found {} comms with calls", commsWithCalls.size());
            
            for (CommsRecord comm : commsWithCalls) {
                Result<Record3<Long, Integer, Integer>> threads = context
                    .select(CommThreads.COMM_THREADS.THREAD_ID, Threads.THREADS.PID, Threads.THREADS.TID)
                    .from(CommThreads.COMM_THREADS)
                    .innerJoin(Threads.THREADS).on(CommThreads.COMM_THREADS.THREAD_ID.eq(Threads.THREADS.ID.cast(Long.class)))
                    .where(CommThreads.COMM_THREADS.COMM_ID.eq(comm.getId().longValue()))
                    .fetch();
                
                result.add(new CommandData(comm.getId(), comm.getComm(), threads));
            }
        }
        
        return result;
    }
    
    public Long calculateTotalTimeNanos(String databasePath, Long commId, Long threadId) {
        logger.debug("Calculating total time for comm: {}, thread: {}", commId, threadId);
        try (DatabaseConnectionHolder dbConnection = new DatabaseConnectionHolder(databasePath)) {
            DSLContext queryContext = dbConnection.getContext();

            Result<?> nodes = queryContext
                .select(DSL.sum(Calls.CALLS.RETURN_TIME.minus(Calls.CALLS.CALL_TIME)))
                .from(Calls.CALLS)
                .where(Calls.CALLS.PARENT_CALL_PATH_ID.eq(ROOT_PARENT_CALL_PATH_ID))
                .and(Calls.CALLS.COMM_ID.eq(commId))
                .and(Calls.CALLS.THREAD_ID.eq(threadId))
                .groupBy(Calls.CALLS.CALL_PATH_ID)
                .fetch();
            
            Long totalTimeNanos = 0L;
            for (org.jooq.Record record : nodes) {
                totalTimeNanos += record.get(0, Long.class);
            }
            return totalTimeNanos != 0L ? totalTimeNanos: 1L;
        } catch (Exception e) {
            logger.error("Error calculating total time: {}", e.getMessage(), e);
            return 1L;
        }
    }

    public List<CallTreeData> loadCallTreeNodes(
        String databasePath,
        Long commId,
        Long threadId,
        Long parentCallPathId,
        Long fromCallTime,
        Long toReturnTime) throws SQLException 
    {
        logger.debug("Loading call tree nodes for comm: {}, thread: {}, parent: {}", commId, threadId, parentCallPathId);

        // TODO rewrite this
        try (DatabaseConnectionHolder dbConnection = new DatabaseConnectionHolder(databasePath)) {
            DSLContext queryContext = dbConnection.getContext();

            if (fromCallTime != null) {
                return queryContext.fetch(
                    "SELECT call_path_id, name, short_name, return_time - call_time, " +
                            "insn_count, cyc_count, branch_count, call_time, return_time " +
                            "FROM calls " +
                            "INNER JOIN call_paths ON calls.call_path_id = call_paths.id " +
                            "INNER JOIN symbols ON call_paths.symbol_id = symbols.id " +
                            "INNER JOIN dsos ON symbols.dso_id = dsos.id " +
                            "WHERE parent_call_path_id = ? " +
                            "AND comm_id = ? " +
                            "AND thread_id = ? " +
                            "AND call_time >= ? " +
                            "AND return_time <= ? " +
                            "ORDER BY call_time, call_path_id",
                    parentCallPathId, commId, threadId, fromCallTime, toReturnTime).map(
                            record -> {
                                Long callPathId = record.get(0, Long.class);
                                String name = record.get(1, String.class);
                                String shortName = record.get(2, String.class);
                                Long totalTime = record.get(3, Long.class);
                                Long totalInsnCount = record.get(4, Long.class);
                                Long totalCycCount = record.get(5, Long.class);
                                Long totalBranchCount = record.get(6, Long.class);
                                Long callTime = record.get(7, Long.class);
                                Long returnTime = record.get(8, Long.class);

                                return new CallTreeData(name, totalTime, callPathId, totalTime, callTime, returnTime);
                            }
                    );
            } else {
                return queryContext.fetch(
                    "SELECT call_path_id, name, short_name, return_time - call_time, " +
                            "insn_count, cyc_count, branch_count, call_time, return_time " +
                            "FROM calls " +
                            "INNER JOIN call_paths ON calls.call_path_id = call_paths.id " +
                            "INNER JOIN symbols ON call_paths.symbol_id = symbols.id " +
                            "INNER JOIN dsos ON symbols.dso_id = dsos.id " +
                            "WHERE parent_call_path_id = ? " +
                            "AND comm_id = ? " +
                            "AND thread_id = ? " +
                            "ORDER BY call_time, call_path_id",
                    parentCallPathId, commId, threadId).map(
                            record -> {
                                Long callPathId = record.get(0, Long.class);
                                String name = record.get(1, String.class);
                                String shortName = record.get(2, String.class);
                                Long totalTime = record.get(3, Long.class);
                                Long totalInsnCount = record.get(4, Long.class);
                                Long totalCycCount = record.get(5, Long.class);
                                Long totalBranchCount = record.get(6, Long.class);
                                Long callTime = record.get(7, Long.class);
                                Long returnTime = record.get(8, Long.class);

                                return new CallTreeData(name, totalTime, callPathId, totalTime, callTime, returnTime);
                            }
                    );
            }
        }
    }
} 