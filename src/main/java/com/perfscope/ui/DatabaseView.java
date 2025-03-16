package com.perfscope.ui;

import com.perfscope.model.CallTreeData;
import com.perfscope.model.DatabaseLoader;
import com.perfscope.model.tables.records.CommsRecord;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseView {
    
    private DatabaseLoader databaseLoader = new DatabaseLoader();
    
    public BorderPane createCommView(String databasePath, CommsRecord comm, Result<Record3<Long, Integer, Integer>> threads) {
        BorderPane tabContent = new BorderPane();
        
        ListView<String> threadListView = new ListView<>();
        
        for (Record3<Long, Integer, Integer> thread : threads) {
            threadListView.getItems().add(String.format("PID: %d, TID: %d", 
                thread.value2(), thread.value3()));
        }
        
        // Create a placeholder for the main content
        TreeView<CallTreeData> callTreeView = new TreeView<>();
        callTreeView.setRoot(new TreeItem<>(new CallTreeData("Call Tree", 0L, 0L)));
        callTreeView.setShowRoot(false);
        
        // Set up custom cell factory for the TreeView
        callTreeView.setCellFactory(tv -> new CallTreeCell());
        
        // Set up thread selection listener
        threadListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Get the selected thread ID
                int selectedIndex = threadListView.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < threads.size()) {
                    Record3<Long, Integer, Integer> selectedThread = threads.get(selectedIndex);
                    Long threadId = selectedThread.value1();
                    
                    // Clear previous tree
                    callTreeView.setRoot(new TreeItem<>(new CallTreeData("Call Tree", 0L, 0L)));
                    callTreeView.setShowRoot(false);
                    
                    // First, calculate the maximum time across all nodes
                    Long maxTime = databaseLoader.calculateMaxTime(databasePath, comm.getId().longValue(), threadId);
                    
                    // Load root nodes (parent_call_path_id = 1) with the max time
                    loadCallTreeNodes(databasePath, comm.getId().longValue(), threadId, 1L, callTreeView.getRoot(), maxTime);
                }
            }
        });
        
        SplitPane splitPane = new SplitPane();

        VBox threadBox = new VBox();
        Label threadsLabel = new Label("Threads");
        threadsLabel.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");
        threadBox.getChildren().addAll(threadsLabel, threadListView);
        VBox.setVgrow(threadListView, Priority.ALWAYS);
        
        // Add the thread list and call tree to the SplitPane
        splitPane.getItems().addAll(threadBox, callTreeView);
        
        // Set the default divider position to 20%
        splitPane.setDividerPositions(0.2);
        
        // Set minimum width for the thread list
        threadBox.setMinWidth(100);
        threadBox.setMaxWidth(300);
        
        // Set the SplitPane as the tab content
        tabContent.setCenter(splitPane);
        
        return tabContent;
    }
    
    private void loadCallTreeNodes(String databasePath, Long commId, Long threadId, Long parentCallPathId, 
                                  TreeItem<CallTreeData> parentItem, Long maxTime) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath)) {
            DSLContext queryContext = DSL.using(conn, SQLDialect.SQLITE);
            
            // Query for call tree nodes
            Result<?> nodes = queryContext.fetch(
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
                parentCallPathId, commId, threadId
            );
            
            // Add nodes to the tree
            for (org.jooq.Record record : nodes) {
                Long callPathId = record.get(0, Long.class);
                String name = record.get(1, String.class);
                String shortName = record.get(2, String.class);
                Long count = record.get(3, Long.class);
                Long totalTime = record.get(4, Long.class);
                Long totalInsnCount = record.get(5, Long.class);
                Long totalCycCount = record.get(6, Long.class);
                Long totalBranchCount = record.get(7, Long.class);
                
                // Format node text
                String nodeText = String.format("%s [%d calls, %d ns]", name, count, totalTime);
                
                // Create tree item with custom data
                CallTreeData nodeData = new CallTreeData(nodeText, callPathId, totalTime);
                nodeData.setMaxTime(maxTime);
                
                TreeItem<CallTreeData> item = new TreeItem<>(nodeData);
                
                // Add a dummy child to show expand arrow (will be replaced when expanded)
                item.getChildren().add(new TreeItem<>(new CallTreeData("Loading...", 0L, 0L)));
                
                item.expandedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue && item.getChildren().size() == 1 && 
                        item.getChildren().get(0).getValue().getLabel().equals("Loading...")) {
                        // Clear dummy child
                        item.getChildren().clear();
                        
                        // Load children - pass down the same maxTime
                        loadCallTreeNodes(databasePath, commId, threadId, item.getValue().getCallPathId(), item, maxTime);
                        
                        // If no children were added, add a placeholder
                        if (item.getChildren().isEmpty()) {
                            item.getChildren().add(new TreeItem<>(new CallTreeData("(No calls)", 0L, 0L)));
                        }
                    }
                });
                
                parentItem.getChildren().add(item);
            }
        } catch (Exception e) {
            System.err.println("Error loading call tree nodes: " + e.getMessage());
            e.printStackTrace();
            
            // Add error node
            parentItem.getChildren().add(new TreeItem<>(new CallTreeData("Error loading data: " + e.getMessage(), 0L, 0L)));
        }
    }
    
    // Custom cell implementation for the call tree
    private static class CallTreeCell extends TreeCell<CallTreeData> {
        private final StackPane stack = new StackPane();
        private final Rectangle timeBar = new Rectangle();
        private final Label label = new Label();
        
        public CallTreeCell() {
            timeBar.setHeight(20); // Fixed height for the bar
            timeBar.setFill(Color.web("#4285F4", 0.3)); // Google blue with transparency
            timeBar.setArcWidth(5); // Rounded corners
            timeBar.setArcHeight(5);
            
            stack.getChildren().addAll(timeBar, label);
            stack.setAlignment(Pos.CENTER_LEFT);
            stack.setPadding(new Insets(2, 5, 2, 0));
            
            // Make sure the bar is behind the text
            StackPane.setAlignment(timeBar, Pos.CENTER_LEFT);
            timeBar.setTranslateX(0); // Start from the left edge
            
            // Add some styling to the label
            label.setStyle("-fx-font-weight: normal;");
        }
        
        @Override
        protected void updateItem(CallTreeData item, boolean empty) {
            super.updateItem(item, empty);
            
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                label.setText(item.getLabel());
                
                // Only show time bar if time is greater than zero
                if (item.getTime() > 0) {
                    // Calculate width based on the time ratio
                    double ratio = item.getTimeRatio();
                    // Get the width of the tree cell (approximation)
                    double maxWidth = getTreeView().getWidth() - (getTreeView().getRoot().getChildren().size() > 0 ? 
                                                                 getTreeView().getRoot().getChildren().get(0).getGraphic() != null ? 
                                                                 40 : 20 : 20);
                    timeBar.setWidth(Math.max(5, ratio * maxWidth)); // Minimum width of 5 pixels
                    timeBar.setVisible(true);
                } else {
                    timeBar.setVisible(false);
                }
                
                setGraphic(stack);
            }
        }
    }
} 