package com.perfscope.ui;

import com.perfscope.db.DatabaseLoader;
import com.perfscope.model.CallTreeData;
import com.perfscope.util.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.sql.SQLException;
import java.util.List;

public class CallTreeView extends TreeView<CallTreeData> {
    
    private static final Logger logger = LoggerFactory.getLogger(CallTreeView.class);
    private static final String DUMMY_LOADING_NODE_NAME = "Loading...";
    
    private final DatabaseLoader databaseLoader = new DatabaseLoader();
    
    public CallTreeView() {
        setRoot(new TreeItem<>(new CallTreeData("Call Tree", null, null, 0L, 0L)));
        setShowRoot(false);
        
        // Set up custom cell factory for the TreeView
        setCellFactory(tv -> new CallTreeCell());
        
        // Add key event handler for clipboard operations
        setOnKeyPressed(this::handleKeyPress);
    }
    
    private void handleKeyPress(KeyEvent event) {
        // Check for Ctrl+C key combination
        if (event.isControlDown() && event.getCode() == KeyCode.C) {
            copySelectedNodeToClipboard();
            event.consume();
        }
    }
    
    private void copySelectedNodeToClipboard() {
        TreeItem<CallTreeData> selectedItem = getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            CallTreeData data = selectedItem.getValue();
            String textToCopy;
            
            if (data.getCount() != null && data.getTotalTime() != null) {
                textToCopy = String.format("%s [%d calls, %s]", 
                    data.getName(), 
                    data.getCount(), 
                    Duration.ofNanos(data.getTotalTime()));
            } else {
                textToCopy = data.getName();
            }

            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(textToCopy);
            clipboard.setContent(content);
            
            logger.debug("Copied to clipboard: {}", textToCopy);
        }
    }
    
    public void loadThreadData(String databasePath, Long commId, Long threadId) {
        setRoot(new TreeItem<>(new CallTreeData("Call Tree", null, null, 0L, 0L)));
        setShowRoot(false);

        Long totalTimeNanos = databaseLoader.calculateTotalTimeNanos(databasePath, commId, threadId);
        loadCallTreeNodes(databasePath, commId, threadId, 1L, getRoot(), totalTimeNanos);
    }
    
    private void loadCallTreeNodes(String databasePath, Long commId, Long threadId, Long parentCallPathId, 
                                  TreeItem<CallTreeData> parentItem, Long totalTimeNanos) {
        try {
            List<CallTreeData> nodes = databaseLoader.loadCallTreeNodes(databasePath, commId, threadId, parentCallPathId);
            
            for (CallTreeData nodeData : nodes) {
                // Set the max time for proper scaling
                nodeData.setTotalTimeNanos(totalTimeNanos);
                
                TreeItem<CallTreeData> item = new TreeItem<>(nodeData);
                
                // Add a dummy child to show expand arrow (will be replaced when expanded)
                item.getChildren().add(new TreeItem<>(new CallTreeData(DUMMY_LOADING_NODE_NAME, null, null, 0L, 0L)));
                
                item.expandedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue && item.getChildren().size() == 1 && 
                        item.getChildren().get(0).getValue().getName().equals(DUMMY_LOADING_NODE_NAME)) {

                        item.getChildren().clear();

                        loadCallTreeNodes(databasePath, commId, threadId, item.getValue().getCallPathId(), item, totalTimeNanos);
                        
                        // If no children were added, add a placeholder
                        if (item.getChildren().isEmpty()) {
                            item.getChildren().add(new TreeItem<>(
                                    new CallTreeData(
                                            "(No calls)",
                                            null,
                                            null,
                                            0L,
                                            0L)
                                    )
                            );
                        }
                    }
                });
                
                parentItem.getChildren().add(item);
            }
        } catch (SQLException e) {
            logger.error("Error loading call tree nodes: {}", e.getMessage(), e);
            
            // Add error node
            parentItem.getChildren().add(new TreeItem<>(
                    new CallTreeData(
                            "Error loading data: " + e.getMessage(),
                            null,
                            null,
                            0L,
                            0L)
                    )
            );
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
                String labelText;
                if (item.getCount() != null && item.getTotalTime() != null) {
                    labelText = String.format("%s [%d calls, %s]", item.getName(), item.getCount(), Duration.ofNanos(item.getTotalTime()));
                } else {
                    labelText = item.getName();
                }

                label.setText(labelText);
                
                // Only show time bar if time is greater than zero
                if (item.getTimeNanos() > 0) {
                    // Calculate width based on the time ratio
                    double ratio = item.getTimeRatio();
                    // Get the width of the tree cell (approximation)
                    double maxWidth = getTreeView().getWidth() - (getTreeView().getRoot().getChildren().size() > 0 ? 
                                                                 getTreeView().getRoot().getChildren().get(0).getGraphic() != null ? 
                                                                 40 : 20 : 20);
                    timeBar.setWidth(Math.max(1, ratio * maxWidth));
                    timeBar.setVisible(true);
                } else {
                    timeBar.setVisible(false);
                }
                
                setGraphic(stack);
            }
        }
    }
} 