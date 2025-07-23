package com.perfscope.view

import com.perfscope.model.Command
import com.perfscope.model.Thread
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.control.Tab
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

class CommandTab(dbPath: String, command: Command) : Tab() {

    private val threadListView = ThreadListView()
    private val callTreeView = CallTreeView(dbPath)

    init {
        id = command.id.toString()
        text = command.command + " " + " (" + command.id + ")"
        content = createContent(command)
        isClosable = false
        userData = command
    }

    fun highlightSearchThreadIds(threadIds: List<Int>) {
        threadListView.highlightSearchThreadIds(threadIds)
    }

    fun clearSearchHighlights() {
        threadListView.clearSearchHighlights()
    }

    private fun createContent(command: Command): BorderPane {
        val tabContent = BorderPane()
        command.threads.forEach { threadListView.add(it) }

        threadListView.selectionModel.selectedItemProperty().addListener { observable, oldValue, newValue ->
            if (newValue != null) {
                val selectedIndex: Int = threadListView.selectionModel.selectedIndex
                if (selectedIndex >= 0 && selectedIndex < command.threads.size) {
                    val selectedThread: Thread = command.threads[selectedIndex]
                    callTreeView.switchThread(command.id, selectedThread.id)
                }
            }
        }

        val threadsLabel = Label("Threads")
        threadsLabel.style = "-fx-font-weight: bold; -fx-padding: 5px;"

        val threadListBox = VBox()
        threadListBox.children.addAll(threadsLabel, threadListView)
        VBox.setVgrow(threadListView, Priority.ALWAYS)

        threadListBox.minWidth = 100.0
        threadListBox.maxWidth = 300.0

        val splitPane = SplitPane()
        splitPane.items.addAll(threadListBox, callTreeView)
        splitPane.setDividerPositions(0.2)

        tabContent.center = splitPane
        return tabContent
    }
}