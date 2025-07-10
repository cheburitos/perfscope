package com.perfscope.view

import com.perfscope.model.Command
import com.perfscope.model.Thread
import javafx.scene.layout.BorderPane
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.layout.VBox
import javafx.scene.control.ListView

class DatabaseView {

    fun createCommView(dbPath: String, command: Command): BorderPane {
        val tabContent = BorderPane()

        val threadListView = ListView<String?>()

        for (thread in command.threads) {
            threadListView.items += String.format("PID: %d, TID: %d", thread.pid, thread.tid)
        }

        val callTreeView = CallTreeView(dbPath)

        threadListView.selectionModel.selectedItemProperty().addListener { observable, oldValue, newValue ->
            if (newValue != null) {
                val selectedIndex: Int = threadListView.getSelectionModel().selectedIndex
                if (selectedIndex >= 0 && selectedIndex < command.threads!!.size) {
                    val selectedThread: Thread = command.threads[selectedIndex]
                    callTreeView.switchThread(command.id, selectedThread.id)
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