package com.perfscope.view

import com.perfscope.model.Command
import com.perfscope.model.Thread
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

class CommandTabPane : TabPane() {

    init {
        tabClosingPolicy = TabClosingPolicy.UNAVAILABLE
    }

    fun clear() {
        tabs.clear()
    }

    fun addCommandTab(dbPath: String, command: Command) {
        val tab = Tab()
        tab.text = command.command + " " + " (" + command.id + ")"
        tab.content = createCommandTabContent(dbPath, command)
        tab.isClosable = false
        tabs += tab
    }

    private fun createCommandTabContent(dbPath: String, command: Command): BorderPane {
        val tabContent = BorderPane()

        val threadListView = ThreadListView()
        command.threads.forEach { threadListView.add(it) }

        val callTreeView = CallTreeView(dbPath)

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