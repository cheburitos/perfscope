package com.perfscope.view

import com.perfscope.model.Call
import com.perfscope.util.Duration.Companion.ofNanos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.TreeCell
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import kotlin.math.max

class CallTreeCell : TreeCell<Call?>() {
    private val stack = StackPane()
    private val timeBar = Rectangle()
    private val textFlow = TextFlow()

    init {
        timeBar.height = 20.0
        timeBar.fill = Color.web("#4285F4", 0.3)
        timeBar.arcWidth = 5.0
        timeBar.arcHeight = 5.0

        stack.children.addAll(timeBar, textFlow)
        stack.alignment = Pos.CENTER_LEFT
        stack.padding = Insets(2.0, 5.0, 2.0, 0.0)

        StackPane.setAlignment(timeBar, Pos.CENTER_LEFT)
        timeBar.translateX = 0.0
    }

    override fun updateItem(item: Call?, empty: Boolean) {
        super.updateItem(item, empty)
        textFlow.children.clear()

        if (empty || item == null) {
            text = null
            graphic = null
        } else {
            val labelText = Text()
            if (item.totalTime != null) {
                labelText.text = String.format("%s [%s]", item.name, ofNanos(item.totalTime))
            } else {
                labelText.text = item.name
            }
            labelText.styleClass += "perfscope-call-tree"

            textFlow.children += labelText

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