package com.perfscope.ui;

import com.perfscope.db.DatabaseLoader;
import com.perfscope.model.CallTreeData;
import com.perfscope.util.Duration;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class CallTreeView extends TreeView<CallTreeData> {
    
    private static final Logger logger = LoggerFactory.getLogger(CallTreeView.class);
    private static final String DUMMY_LOADING_NODE_NAME = "Loading...";
    
    private final DatabaseLoader databaseLoader = new DatabaseLoader();
    
    public CallTreeView() {
        setRoot(new TreeItem<>(CallTreeData.stub("Call Tree")));
        setShowRoot(false);
        setCellFactory(tv -> new CallTreeCell());
        setOnKeyPressed(this::handleKeyPress);
    }
    
    private void handleKeyPress(KeyEvent event) {
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
            
            if (data.getTotalTime() != null) {
                textToCopy = String.format("%s [%s]", 
                    data.getName(), 
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
        setRoot(new TreeItem<>(CallTreeData.stub("Call Tree")));
        setShowRoot(false);

        Long totalTimeNanos = databaseLoader.calculateTotalTimeNanos(databasePath, commId, threadId);
        loadCallTreeNodes(databasePath, commId, threadId, 1L, getRoot(), totalTimeNanos);
    }
    
    private void loadCallTreeNodes(String databasePath, Long commId, Long threadId, Long parentCallPathId, 
                                  TreeItem<CallTreeData> parentItem, Long totalTimeNanos) {
        try {
            CallTreeData callTreeData = parentItem.getValue();
            List<CallTreeData> nodes = databaseLoader.loadCallTreeNodes(
                databasePath, 
                commId, 
                threadId, 
                parentCallPathId, 
                callTreeData.getCallTime(), 
                callTreeData.getReturnTime()
            );
            
            for (CallTreeData nodeData : nodes) {
                nodeData.setTotalTimeNanos(totalTimeNanos);
                
                TreeItem<CallTreeData> item = new TreeItem<>(nodeData);
                
                // Add a dummy child to show expand arrow (will be replaced when expanded)
                item.getChildren().add(new TreeItem<>(CallTreeData.stub(DUMMY_LOADING_NODE_NAME)));
                
                item.expandedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue && item.getChildren().size() == 1 && 
                        item.getChildren().get(0).getValue().getName().equals(DUMMY_LOADING_NODE_NAME)) {
                        item.getChildren().clear();

                        loadCallTreeNodes(databasePath, commId, threadId, item.getValue().getCallPathId(), item, totalTimeNanos);
                        
                        if (item.getChildren().isEmpty()) {
                            item.getChildren().add(new TreeItem<>(CallTreeData.stub("(No calls)")));
                        }
                    }
                });
                
                parentItem.getChildren().add(item);
            }
        } catch (SQLException e) {
            logger.error("Error loading call tree nodes: {}", e.getMessage(), e);
            
            parentItem.getChildren().add(new TreeItem<>(CallTreeData.stub("Error loading data: " + e.getMessage())));
        }
    }
} 