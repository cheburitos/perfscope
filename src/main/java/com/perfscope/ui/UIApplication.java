package com.perfscope.ui;

import com.tracelyp.model.tables.Comms;
import com.tracelyp.model.tables.CommThreads;
import com.tracelyp.model.tables.Threads;
import com.tracelyp.model.tables.records.CommsRecord;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;
import java.io.File;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class UIApplication extends Application {

    public static Stage stage;
    private String currentDatabasePath = "examples/pt_example"; // Default database path

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setTitle("tracelyp");
        
        BorderPane root = new BorderPane();
        
        // Create menu bar
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem openMenuItem = new MenuItem("Open Database...");
        
        openMenuItem.setOnAction(e -> openDatabase());
        
        fileMenu.getItems().add(openMenuItem);
        menuBar.getMenus().add(fileMenu);
        
        root.setTop(menuBar);
        
        // Create tab pane for database content
        TabPane tabPane = new TabPane();
        root.setCenter(tabPane);
        
        // Load the default database
//        loadDatabase(currentDatabasePath, tabPane);
        
        // Set up the scene
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
    
    private void openDatabase() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Database File");
        
        // Set initial directory if current database path exists
        File initialDir = new File(currentDatabasePath).getParentFile();
        if (initialDir != null && initialDir.exists()) {
            fileChooser.setInitialDirectory(initialDir);
        }
        
/*         // Set file extension filters
        FileChooser.ExtensionFilter sqliteFilter = 
            new FileChooser.ExtensionFilter("SQLite Database (*.db, *.sqlite, *.sqlite3)", "*.db", "*.sqlite", "*.sqlite3");
        FileChooser.ExtensionFilter allFilter = 
            new FileChooser.ExtensionFilter("All Files", "*.*");
        fileChooser.getExtensionFilters().addAll(sqliteFilter, allFilter);
 */        
        // Show open file dialog
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
                TreeView<String> callTreeView = new TreeView<>();
                callTreeView.setRoot(new TreeItem<>("Call Tree"));
                callTreeView.setShowRoot(false);
                
                // Set up thread selection listener
                threadListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        // Get the selected thread ID
                        int selectedIndex = threadListView.getSelectionModel().getSelectedIndex();
                        if (selectedIndex >= 0 && selectedIndex < threads.size()) {
                            Record3<Long, Integer, Integer> selectedThread = threads.get(selectedIndex);
                            Long threadId = selectedThread.value1();
                            
                            // Clear previous tree
                            callTreeView.setRoot(new TreeItem<>("Call Tree"));
                            callTreeView.setShowRoot(false);
                            
                            // Load root nodes (parent_call_path_id = 1)
                            loadCallTreeNodes(databasePath, comm.getId().longValue(), threadId, 1L, callTreeView.getRoot());
                        }
                    }
                });
                
                SplitPane splitPane = new SplitPane();

                VBox threadBox = new VBox();
                Label threadsLabel = new Label("Threads");
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

    private void loadCallTreeNodes(String databasePath, Long commId, Long threadId, Long parentCallPathId, TreeItem<String> parentItem) {
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
                CallTreeItem item = new CallTreeItem(nodeText, callPathId);
                
                // Add a dummy child to show expand arrow (will be replaced when expanded)
                item.getChildren().add(new TreeItem<>("Loading..."));
                
                // Add expand listener
                item.expandedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue && item.getChildren().size() == 1 && 
                        item.getChildren().get(0).getValue().equals("Loading...")) {
                        // Clear dummy child
                        item.getChildren().clear();
                        
                        // Load children
                        loadCallTreeNodes(databasePath, commId, threadId, item.getCallPathId(), item);
                        
                        // If no children were added, add a placeholder
                        if (item.getChildren().isEmpty()) {
                            item.getChildren().add(new TreeItem<>("(No calls)"));
                        }
                    }
                });
                
                parentItem.getChildren().add(item);
            }
        } catch (Exception e) {
            System.err.println("Error loading call tree nodes: " + e.getMessage());
            e.printStackTrace();
            
            // Add error node
            parentItem.getChildren().add(new TreeItem<>("Error loading data: " + e.getMessage()));
        }
    }

    // Custom TreeItem class to store call path ID
    private static class CallTreeItem extends TreeItem<String> {
        private final Long callPathId;
        
        public CallTreeItem(String value, Long callPathId) {
            super(value);
            this.callPathId = callPathId;
        }
        
        public Long getCallPathId() {
            return callPathId;
        }
    }
}
