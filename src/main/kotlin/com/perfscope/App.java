package com.perfscope;

import com.perfscope.db.DatabaseLoader;
import com.perfscope.model.CommandData;
import com.perfscope.view.DatabaseView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class App extends Application {

    private static final Logger logger = LoggerFactory.getLogger(App.class);
    public static Stage stage;

    private String dbPath = "examples/pt_example";
    private DatabaseLoader databaseLoader;
    private DatabaseView databaseView;

    public static void main(String[] args) {
        logger.info("Starting PerfScope application");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Initializing application UI");
        stage = primaryStage;
        stage.setTitle("PerfScope");

        databaseView = new DatabaseView();
        
        Scene scene = new Scene(createRootPane(), 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        stage.setScene(scene);
        stage.show();
    }
    
    private BorderPane createRootPane() {
        BorderPane root = new BorderPane();
        
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem openMenuItem = new MenuItem("Open Database...");
        openMenuItem.setGraphic(createIcon("folder-open", 16));
        openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        
        openMenuItem.setOnAction(e -> openDatabase());
        
        fileMenu.getItems().add(openMenuItem);
        menuBar.getMenus().add(fileMenu);

        HBox statusBar = new HBox();
        statusBar.setId("status-bar");
        statusBar.setPadding(new Insets(5));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        Label statusLabel = new Label("Ready");
        statusBar.getChildren().add(statusLabel);
        
        root.setTop(menuBar);
        root.setBottom(statusBar);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        root.setCenter(tabPane);
        
        return root;
    }
    
    private ImageView createIcon(String name, int size) {
        try {
            Image image = new Image(getClass().getResourceAsStream("/icons/" + name + ".png"));
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(size);
            imageView.setFitWidth(size);
            return imageView;
        } catch (Exception e) {
            logger.warn("Could not load icon: {}", name, e);
            return null;
        }
    }

    private void openDatabase() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Database File");
        
        // Set initial directory if current database path exists
        File initialDir = new File(dbPath).getParentFile();
        if (initialDir != null && initialDir.exists()) {
            fileChooser.setInitialDirectory(initialDir);
        }

        File file = fileChooser.showOpenDialog(stage);
        
        if (file != null) {
            dbPath = file.getAbsolutePath();
            logger.info("Opening database: {}", dbPath);

            TabPane tabPane = (TabPane) ((BorderPane) stage.getScene().getRoot()).getCenter();
            tabPane.getTabs().clear();
            
            updateStatus("Loading database: " + file.getName());

            databaseLoader = new DatabaseLoader(dbPath);

            // TODO software shit
            // Load database in a background thread
            new Thread(() -> {
                try {

                    List<CommandData> commsWithCalls = databaseLoader.fetchCommands(dbPath);
                    
                    // Update UI on JavaFX thread
                    Platform.runLater(() -> {
                        for (CommandData commandData : commsWithCalls) {
                            Tab tab = new Tab();
                            tab.setText(commandData.getCommand() + " " + " (" + commandData.getId() + ")");
                            tab.setContent(databaseView.createCommView(dbPath, commandData));
                            tab.setClosable(false);
                            tabPane.getTabs().add(tab);
                        }
                        updateStatus("Database loaded: " + file.getName());
                    });
                } catch (SQLException e) {
                    logger.error("Error loading database: {}", e.getMessage(), e);
                    
                    // Update UI on JavaFX thread
                    Platform.runLater(() -> {
                        Tab errorTab = new Tab("Error");
                        errorTab.setContent(new Label("Database error: " + e.getMessage()));
                        errorTab.setClosable(false);
                        tabPane.getTabs().add(errorTab);
                        updateStatus("Error loading database");
                    });
                }
            }).start();
        }
    }
    
    private void updateStatus(String message) {
        Platform.runLater(() -> {
            HBox statusBar = (HBox) ((BorderPane) stage.getScene().getRoot()).getBottom();
            Label statusLabel = (Label) statusBar.getChildren().get(0);
            statusLabel.setText(message);
        });
    }
}
