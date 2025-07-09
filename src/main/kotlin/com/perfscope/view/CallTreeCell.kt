package com.perfscope.view

import com.perfscope.model.CallTreeData
import com.perfscope.util.Duration.Companion.ofNanos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.TreeCell
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import kotlin.math.max

class CallTreeCell : TreeCell<CallTreeData?>() {
    private val stack = StackPane()
    private val timeBar = Rectangle()
    private val label = Label()

    init {
        timeBar.height = 20.0
        timeBar.fill = Color.web("#4285F4", 0.3)
        timeBar.arcWidth = 5.0
        timeBar.arcHeight = 5.0

        stack.children.addAll(timeBar, label)
        stack.alignment = Pos.CENTER_LEFT
        stack.padding = Insets(2.0, 5.0, 2.0, 0.0)

        StackPane.setAlignment(timeBar, Pos.CENTER_LEFT)
        timeBar.translateX = 0.0

        label.style = "-fx-font-weight: normal; -fx-font-size: 15px;"
    }

    override fun updateItem(item: CallTreeData?, empty: Boolean) {
        super.updateItem(item, empty)

        if (empty || item == null) {
            text = null
            graphic = null
        } else {
            val labelText: String?
            if (item.totalTime != null) {
                labelText = String.format("%s [%s]", item.name, ofNanos(item.totalTime))
            } else {
                labelText = item.name
            }

            label.text = labelText

            if (item.timeNanos != null && item.timeNanos > 0) {
                val ratio: Double = item.timeRatio
                val maxWidth: Double = treeView.width - (if (treeView.getRoot().getChildren().isNotEmpty()) if
                        (treeView.getRoot().getChildren()[0].graphic != null) 40 else 20 else 20)
                timeBar.width = max(1.0, ratio * maxWidth)
                timeBar.isVisible = true
            } else {
                timeBar.isVisible = false
            }

            graphic = stack
        }
    }
}