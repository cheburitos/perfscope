package com.perfscope.view

import com.perfscope.model.Command
import javafx.scene.control.*

class CommandTabPane : TabPane() {

    init {
        tabClosingPolicy = TabClosingPolicy.UNAVAILABLE
    }

    fun clear() {
        tabs.clear()
    }

    fun addCommandTab(dbPath: String, command: Command) {
        tabs += CommandTab(dbPath, command)
    }

    fun getSelectedCommand(): Command? {
        val tab = selectionModel.selectedItem
        return tab.userData as Command?
    }

    fun highlightSearchThreadIds(commandId: Long, threadIds: List<Int>) {
        val tab = tabs.find { it.id == commandId.toString() } as CommandTab
        tab.highlightSearchThreadIds(threadIds)
    }
}