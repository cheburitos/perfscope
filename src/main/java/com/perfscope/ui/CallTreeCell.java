package com.perfscope.ui;

import com.perfscope.model.CallTreeData;
import com.perfscope.util.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CallTreeCell extends TreeCell<CallTreeData> {
    private final StackPane stack = new StackPane();
    private final Rectangle timeBar = new Rectangle();
    private final Label label = new Label();
    
    public CallTreeCell() {
        timeBar.setHeight(20);
        timeBar.setFill(Color.web("#4285F4", 0.3));
        timeBar.setArcWidth(5);
        timeBar.setArcHeight(5);
        
        stack.getChildren().addAll(timeBar, label);
        stack.setAlignment(Pos.CENTER_LEFT);
        stack.setPadding(new Insets(2, 5, 2, 0));
        
        StackPane.setAlignment(timeBar, Pos.CENTER_LEFT);
        timeBar.setTranslateX(0);
        
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
            if (item.getTotalTime() != null) {
                labelText = String.format("%s [%s]", item.getName(), Duration.ofNanos(item.getTotalTime()));
            } else {
                labelText = item.getName();
            }

            label.setText(labelText);
            
            if (item.getTimeNanos() != null && item.getTimeNanos() > 0) {
                double ratio = item.getTimeRatio();
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