package com.perfscope.db;

import com.perfscope.model.CallTreeData;
import com.perfscope.model.tables.Calls;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class DatabaseLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseLoader.class);

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