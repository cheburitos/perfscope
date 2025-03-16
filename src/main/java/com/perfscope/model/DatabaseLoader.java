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
import java.util.List;

public class DatabaseLoader {
    
    public void loadDatabase(String databasePath, TabPane tabPane, DatabaseView databaseView) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath)) {
            DSLContext context = DSL.using(conn, SQLDialect.SQLITE);
            
            List<CommsRecord> commsWithCalls = context
                .selectFrom(Comms.COMMS)
                .where(Comms.COMMS.HAS_CALLS.eq(true))
                .fetch();
            
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
            // Handle database connection errors
            Tab errorTab = new Tab("Error");
            errorTab.setContent(new Label("Database error: " + e.getMessage()));
            errorTab.setClosable(false);
            tabPane.getTabs().add(errorTab);
            e.printStackTrace();
        }
    }
    
    public Long calculateMaxTime(String databasePath, Long commId, Long threadId) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath)) {
            DSLContext queryContext = DSL.using(conn, SQLDialect.SQLITE);
            
            // Query for call tree nodes
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
            System.err.println("Error calculating max time: " + e.getMessage());
            e.printStackTrace();
            return 1L; // Default to 1 on error
        }
    }
} 