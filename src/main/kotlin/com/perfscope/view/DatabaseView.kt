package com.perfscope.view

import org.jooq.Record3
import com.perfscope.model.CommandData
import javafx.scene.layout.BorderPane
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.layout.VBox
import javafx.beans.value.ObservableValue
import javafx.scene.control.ListView

class DatabaseView {
    fun createCommView(databasePath: String, commandData: CommandData): BorderPane {
        val tabContent = BorderPane()

        val threadListView = ListView<String?>()

        for (thread in commandData.threads!!) {
            threadListView.items += String.format("PID: %d, TID: %d", thread!!.value2(), thread!!.value3())
        }

        val callTreeView = CallTreeView()

        threadListView.selectionModel.selectedItemProperty().addListener { observable, oldValue, newValue ->
            if (newValue != null) {
                val selectedIndex: Int = threadListView.getSelectionModel().selectedIndex
                if (selectedIndex >= 0 && selectedIndex < commandData.threads!!.size) {
                    val selectedThread: Record3<Long?, Int?, Int?>? = commandData.threads.get(selectedIndex)
                    val threadId = selectedThread!!.value1()!!

                    callTreeView.loadThreadData(databasePath, commandData.id, threadId)
                }
            }
        }

        val threadsLabel = Label("Threads")
        threadsLabel.style = "-fx-font-weight: bold; -fx-padding: 5px;"

        val threadBox = VBox()
        threadBox.children.addAll(threadsLabel, threadListView)
        VBox.setVgrow(threadListView, javafx.scene.layout.Priority.ALWAYS)

        threadBox.minWidth = 100.0
        threadBox.maxWidth = 300.0

        val splitPane = SplitPane()
        splitPane.items.addAll(threadBox, callTreeView)
        splitPane.setDividerPositions(0.2)

        tabContent.center = splitPane
        return tabContent
    }
}