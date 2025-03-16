package com.perfscope;
import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.File;

import com.perfscope.ui.DatabaseView;
import com.perfscope.model.DatabaseLoader;

public class App extends Application {

    public static Stage stage;
    private String currentDatabasePath = "examples/pt_example"; // Default database path
    private DatabaseLoader databaseLoader;
    private DatabaseView databaseView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setTitle("PerfScope");
        
        databaseLoader = new DatabaseLoader();
        databaseView = new DatabaseView();
        
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
        openMenuItem.setGraphic(createIcon("folder-open", 16));
        openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        
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
    
    private ImageView createIcon(String name, int size) {
        try {
            Image image = new Image(getClass().getResourceAsStream("/icons/" + name + ".png"));
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(size);
            imageView.setFitWidth(size);
            return imageView;
        } catch (Exception e) {
            return null;
        }
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
            
            updateStatus("Loading database: " + file.getName());
            databaseLoader.loadDatabase(currentDatabasePath, tabPane, databaseView);
            updateStatus("Database loaded: " + file.getName());
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
