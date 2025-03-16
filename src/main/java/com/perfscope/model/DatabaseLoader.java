package com.perfscope.model;

import com.perfscope.model.tables.Comms;
import com.perfscope.model.tables.CommThreads;
import com.perfscope.model.tables.Threads;
import com.perfscope.model.tables.records.CommsRecord;
import com.perfscope.ui.DatabaseView;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseLoader.class);
    
    public void loadDatabase(String databasePath, TabPane tabPane, DatabaseView databaseView) {
        logger.info("Loading database from: {}", databasePath);
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath)) {
            DSLContext context = DSL.using(conn, SQLDialect.SQLITE);
            
            List<CommsRecord> commsWithCalls = context
                .selectFrom(Comms.COMMS)
                .where(Comms.COMMS.HAS_CALLS.eq(true))
                .fetch();
            
            logger.info("Found {} comms with calls", commsWithCalls.size());
            
            for (CommsRecord comm : commsWithCalls) {
                Tab tab = new Tab();
                tab.setText(comm.getComm() + " (" + comm.getId() + ")");
                
                Result<Record3<Long, Integer, Integer>> threads = context
                    .select(CommThreads.COMM_THREADS.THREAD_ID, Threads.THREADS.PID, Threads.THREADS.TID)
                    .from(CommThreads.COMM_THREADS)
                    .innerJoin(Threads.THREADS).on(CommThreads.COMM_THREADS.THREAD_ID.eq(Threads.THREADS.ID.cast(Long.class)))
                    .where(CommThreads.COMM_THREADS.COMM_ID.eq(comm.getId().longValue()))
                    .fetch();
                
                tab.setContent(databaseView.createCommView(databasePath, comm, threads));
                tab.setClosable(false);
                tabPane.getTabs().add(tab);
            }
        } catch (Exception e) {
            logger.error("Error loading database: {}", e.getMessage(), e);
            // Handle database connection errors
            Tab errorTab = new Tab("Error");
            errorTab.setContent(new Label("Database error: " + e.getMessage()));
            errorTab.setClosable(false);
            tabPane.getTabs().add(errorTab);
        }
    }
    
    public Long calculateMaxTime(String databasePath, Long commId, Long threadId) {
        logger.debug("Calculating max time for comm: {}, thread: {}", commId, threadId);
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath)) {
            DSLContext queryContext = DSL.using(conn, SQLDialect.SQLITE);

            Result<?> nodes = queryContext.fetch(
                "SELECT SUM(return_time - call_time)" +
                "FROM calls " +
                "INNER JOIN call_paths ON calls.call_path_id = call_paths.id " +
                "INNER JOIN symbols ON call_paths.symbol_id = symbols.id " +
                "INNER JOIN dsos ON symbols.dso_id = dsos.id " +
                "WHERE parent_call_path_id = ? " +
                "AND comm_id = ? " +
                "AND thread_id = ? " +
                "GROUP BY call_path_id, name, short_name " +
                "ORDER BY call_time, call_path_id",
                1L, commId, threadId
            );
            
            Long maxTime = 0L;
            for (org.jooq.Record record : nodes) {
                maxTime += record.get(0, Long.class);
            }
            return maxTime != 0L ? maxTime: 1L; // Avoid division by zero
        } catch (Exception e) {
            logger.error("Error calculating max time: {}", e.getMessage(), e);
            return 1L;
        }
    }

    public List<CallTreeData> loadCallTreeNodes(String databasePath, Long commId, Long threadId, Long parentCallPathId) throws SQLException {
        logger.debug("Loading call tree nodes for comm: {}, thread: {}, parent: {}", commId, threadId, parentCallPathId);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath)) {
            DSLContext queryContext = DSL.using(conn, SQLDialect.SQLITE);

            return queryContext.fetch(
                    "SELECT call_path_id, name, short_name, COUNT(calls.id), SUM(return_time - call_time), " +
                            "SUM(insn_count), SUM(cyc_count), SUM(branch_count) " +
                            "FROM calls " +
                            "INNER JOIN call_paths ON calls.call_path_id = call_paths.id " +
                            "INNER JOIN symbols ON call_paths.symbol_id = symbols.id " +
                            "INNER JOIN dsos ON symbols.dso_id = dsos.id " +
                            "WHERE parent_call_path_id = ? " +
                            "AND comm_id = ? " +
                            "AND thread_id = ? " +
                            "GROUP BY call_path_id, name, short_name " +
                            "ORDER BY call_time, call_path_id",
                    parentCallPathId, commId, threadId).map(
                            record -> {
                                Long callPathId = record.get(0, Long.class);
                                String name = record.get(1, String.class);
                                String shortName = record.get(2, String.class);
                                Long count = record.get(3, Long.class);
                                Long totalTime = record.get(4, Long.class);
                                Long totalInsnCount = record.get(5, Long.class);
                                Long totalCycCount = record.get(6, Long.class);
                                Long totalBranchCount = record.get(7, Long.class);

                                String nodeText = String.format("%s [%d calls, %d ns]", name, count, totalTime);
                                return new CallTreeData(nodeText, callPathId, totalTime);
                            }
                    );
        }
    }
} 