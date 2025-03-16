package com.perfscope;

import com.perfscope.model.tables.Comms;
import com.perfscope.model.tables.CommThreads;
import com.perfscope.model.tables.Threads;
import com.perfscope.model.tables.records.CommsRecord;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Pos;
import javafx.scene.control.TreeCell;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class App extends Application {

    public static Stage stage;
    private String currentDatabasePath = "examples/pt_example"; // Default database path

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setTitle("PerfScope");
        
        // Apply modern styling
        Scene scene = new Scene(createRootPane(), 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        stage.setScene(scene);
        stage.show();
    }
    
    private BorderPane createRootPane() {
        BorderPane root = new BorderPane();
        
        // Create menu bar with icons
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem openMenuItem = new MenuItem("Open Database...");        
        openMenuItem.setOnAction(e -> openDatabase());
        
        fileMenu.getItems().add(openMenuItem);
        menuBar.getMenus().add(fileMenu);
        
        // Add status bar at bottom
        HBox statusBar = new HBox();
        statusBar.setId("status-bar");
        statusBar.setPadding(new Insets(5));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        Label statusLabel = new Label("Ready");
        statusBar.getChildren().add(statusLabel);
        
        root.setTop(menuBar);
        root.setBottom(statusBar);
        
        // Create tab pane for database content
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        root.setCenter(tabPane);
        
        return root;
    }
    
    private void openDatabase() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Database File");
        
        // Set initial directory if current database path exists
        File initialDir = new File(currentDatabasePath).getParentFile();
        if (initialDir != null && initialDir.exists()) {
            fileChooser.setInitialDirectory(initialDir);
        }

        File file = fileChooser.showOpenDialog(stage);
        
        if (file != null) {
            currentDatabasePath = file.getAbsolutePath();
            
            // Clear existing tabs and load the new database
            TabPane tabPane = (TabPane) ((BorderPane) stage.getScene().getRoot()).getCenter();
            tabPane.getTabs().clear();
            loadDatabase(currentDatabasePath, tabPane);
        }
    }
    
    private void loadDatabase(String databasePath, TabPane tabPane) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath)) {
            DSLContext context = DSL.using(conn, SQLDialect.SQLITE);
            
            List<CommsRecord> commsWithCalls = context
                .selectFrom(Comms.COMMS)
                .where(Comms.COMMS.HAS_CALLS.eq(true))
                .fetch();
            
            for (CommsRecord comm : commsWithCalls) {
                Tab tab = new Tab();
                tab.setText(comm.getComm() + " (" + comm.getId() + ")");
                
                BorderPane tabContent = new BorderPane();
                
                ListView<String> threadListView = new ListView<>();
                
                Result<Record3<Long, Integer, Integer>> threads = context
                    .select(CommThreads.COMM_THREADS.THREAD_ID, Threads.THREADS.PID, Threads.THREADS.TID)
                    .from(CommThreads.COMM_THREADS)
                    .innerJoin(Threads.THREADS).on(CommThreads.COMM_THREADS.THREAD_ID.eq(Threads.THREADS.ID.cast(Long.class)))
                    .where(CommThreads.COMM_THREADS.COMM_ID.eq(comm.getId().longValue()))
                    .fetch();
                
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
                            Long maxTime = calculateMaxTime(databasePath, comm.getId().longValue(), threadId);
                            
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
                
                Label detailsLabel = new Label("Details for " + comm.getComm());
                
                // Add the thread list and call tree to the SplitPane
                splitPane.getItems().addAll(threadBox, callTreeView);
                
                // Set the default divider position to 20%
                splitPane.setDividerPositions(0.2);
                
                // Set minimum width for the thread list
                threadBox.setMinWidth(100);
                threadBox.setMaxWidth(300);
                
                // Set the SplitPane as the tab content
                tabContent.setCenter(splitPane);
                tab.setContent(tabContent);
                
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

    // Custom data class for call tree nodes
    private static class CallTreeData {
        private final String label;
        private final Long callPathId;
        private final Long time;
        private Long maxTime = 1L;
        
        public CallTreeData(String label, Long callPathId, Long time) {
            this.label = label;
            this.callPathId = callPathId;
            this.time = time;
        }
        
        public String getLabel() {
            return label;
        }
        
        public Long getCallPathId() {
            return callPathId;
        }
        
        public Long getTime() {
            return time;
        }
        
        public void setMaxTime(Long maxTime) {
            this.maxTime = maxTime;
        }
        
        public double getTimeRatio() {
            return (double) time / maxTime;
        }
        
        @Override
        public String toString() {
            return label;
        }
    }

    // Custom cell implementation for the call tree
    private static class CallTreeCell extends TreeCell<CallTreeData> {
        private final StackPane stack = new StackPane();
        private final Rectangle timeBar = new Rectangle();
        private final Label label = new Label();
        
        public CallTreeCell() {
            timeBar.setHeight(20); // Fixed height for the bar
            timeBar.setFill(Color.LIGHTBLUE.deriveColor(0, 1, 1, 0.3)); // Semi-transparent light blue
            
            stack.getChildren().addAll(timeBar, label);
            stack.setAlignment(Pos.CENTER_LEFT);
            
            // Make sure the bar is behind the text
            StackPane.setAlignment(timeBar, Pos.CENTER_LEFT);
            timeBar.setTranslateX(0); // Start from the left edge
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

    private Long calculateMaxTime(String databasePath, Long commId, Long threadId) {
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
}
