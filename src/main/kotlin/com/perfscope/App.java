package com.perfscope;

import com.perfscope.db.DatabaseLoader;
import com.perfscope.model.Command;
import com.perfscope.view.CommandTab;
import com.perfscope.view.CommandTabPane;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

public class App extends Application {

    private static final Logger logger = LoggerFactory.getLogger(App.class);
    public static Stage stage;

    private String dbPath = "examples/pt_example";
    private DatabaseLoader databaseLoader;
    private Context context = new Context();

    public static void main(String[] args) {
        logger.info("Starting PerfScope application");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Initializing application UI");
        stage = primaryStage;
        stage.setTitle("PerfScope");
        Scene scene = context.getSceneRegistry().newScene(createRootPane(), 1000, 700);
        stage.setScene(scene);
        stage.show();

        context.onAfterStart();
    }
    
    private BorderPane createRootPane() {
        BorderPane root = new BorderPane();
        
        MenuBar menuBar = new MenuBar();
        
        // File Menu
        Menu fileMenu = new Menu("File");
        MenuItem openMenuItem = new MenuItem("Open Database...");
        openMenuItem.setGraphic(createIcon("folder-open", 16));
        openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        openMenuItem.setOnAction(e -> openDatabase());
        fileMenu.getItems().add(openMenuItem);
        
        // Search Menu
        Menu searchMenu = new Menu("Search");
        MenuItem findMenuItem = new MenuItem("Find in Call Tree...");
        findMenuItem.setGraphic(createIcon("search", 16));
        findMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
        findMenuItem.setOnAction(e -> openSearchDialog());
        searchMenu.getItems().add(findMenuItem);
        
        menuBar.getMenus().addAll(fileMenu, searchMenu);

        HBox statusBar = new HBox();
        statusBar.setId("status-bar");
        statusBar.setPadding(new Insets(5));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        Label statusLabel = new Label("Ready");
        statusBar.getChildren().add(statusLabel);
        
        root.setTop(menuBar);
        root.setBottom(statusBar);

        root.setCenter(new CommandTabPane());
        
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

            CommandTabPane commandTabPane = (CommandTabPane) ((BorderPane) stage.getScene().getRoot()).getCenter();
            commandTabPane.clear();
            
            updateStatus("Loading database: " + file.getName());

            databaseLoader = new DatabaseLoader(dbPath);

            // TODO software shit
            // Load database in a background thread
            new Thread(() -> {
                try {

                    List<Command> commsWithCalls = databaseLoader.fetchCommands(dbPath);
                    
                    // Update UI on JavaFX thread
                    Platform.runLater(() -> {
                        for (Command command : commsWithCalls) {
                            commandTabPane.addCommandTab(dbPath, command);
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
                        commandTabPane.getTabs().add(errorTab);
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

    private void openSearchDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Search in Call Tree");
        dialog.setHeaderText("Enter search term");
        
        ButtonType findButtonType = new ButtonType("Find", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(findButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField searchField = new TextField();
        searchField.setPromptText("Enter method name or pattern...");
        searchField.setPrefWidth(300);
        
        grid.add(new Label("Search term:"), 0, 0);
        grid.add(searchField, 1, 0);
        
        dialog.getDialogPane().setContent(grid);

        Platform.runLater(searchField::requestFocus);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == findButtonType) {
                return searchField.getText();
            }
            return null;
        });
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(searchTerm -> {
            if (!searchTerm.trim().isEmpty()) {
                performSearch(searchTerm.trim());
            }
        });
    }
    
    private void performSearch(String searchTerm) {
        logger.info("Performing search for: {}", searchTerm);
        
        CommandTabPane commandTabPane = (CommandTabPane) ((BorderPane) stage.getScene().getRoot()).getCenter();
        CommandTab commandTab = (CommandTab) commandTabPane.getSelectionModel().getSelectedItem();
        commandTab.clearSearchHighlights();

        Command command = commandTabPane.getSelectedCommand();
        if (command == null) {
            return;
        }

        updateStatus("Searching for: " + searchTerm + " in command " + command.getId());

        new Thread(() -> {
            try {
                runSearch(searchTerm, command, commandTabPane);
            } catch (Exception e) {
                updateStatus("Search failed");
                logger.error("Search failed", e);
            }
        }).start();
    }

    private void runSearch(String searchTerm, Command command, CommandTabPane commandTabPane) {
        for (com.perfscope.model.Thread thread : command.getThreads()) {
            updateStatus("Searching in thread " + thread.getTid() + "...");
            boolean foundInThread = databaseLoader.search(command.getId(), thread.getId(), searchTerm);
            logger.info("Found result {} in thread: {}", foundInThread, thread.getTid());
            if (foundInThread) {
                Platform.runLater(() -> {
                    commandTabPane.highlightSearchThreadIds(command.getId(), Collections.singletonList(thread.getTid()));
                });
            }
        }
        updateStatus("Search finished");
    }
}
