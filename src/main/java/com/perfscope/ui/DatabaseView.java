package com.perfscope.ui;

import com.perfscope.model.CommData;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.jooq.Record3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseView {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseView.class);

    public BorderPane createCommView(String databasePath, CommData commData) {
        BorderPane tabContent = new BorderPane();
        
        ListView<String> threadListView = new ListView<>();
        
        for (Record3<Long, Integer, Integer> thread : commData.getThreads()) {
            threadListView.getItems().add(String.format("PID: %d, TID: %d", thread.value2(), thread.value3()));
        }
        
        CallTreeView callTreeView = new CallTreeView();
        
        threadListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int selectedIndex = threadListView.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < commData.getThreads().size()) {
                    Record3<Long, Integer, Integer> selectedThread = commData.getThreads().get(selectedIndex);
                    Long threadId = selectedThread.value1();
                    
                    callTreeView.loadThreadData(databasePath, commData.getComm().getId().longValue(), threadId);
                }
            }
        });
        
        SplitPane splitPane = new SplitPane();

        VBox threadBox = new VBox();
        Label threadsLabel = new Label("Threads");
        threadsLabel.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");
        threadBox.getChildren().addAll(threadsLabel, threadListView);
        VBox.setVgrow(threadListView, Priority.ALWAYS);
        
        splitPane.getItems().addAll(threadBox, callTreeView);
        splitPane.setDividerPositions(0.2);

        threadBox.setMinWidth(100);
        threadBox.setMaxWidth(300);

        tabContent.setCenter(splitPane);
        
        return tabContent;
    }
} 