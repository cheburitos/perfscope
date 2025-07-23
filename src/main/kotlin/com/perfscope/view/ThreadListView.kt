package com.perfscope.view

import com.perfscope.model.Thread
import javafx.scene.control.ListView
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.scene.paint.Color
import javafx.scene.control.ListCell

class ThreadListView : ListView<ThreadListItem>() {
    
    private val threadItems = mutableMapOf<Int, ThreadListItem>()

    init {
        setCellFactory { ThreadListCell() }
    }

    fun add(thread: Thread) {
        val item = ThreadListItem(thread.pid, thread.tid)
        threadItems[thread.tid] = item
        items += item
    }

    fun highlightSearchThreadIds(threadIds: List<Int>) {
        threadIds.forEach { threadId ->
            threadItems[threadId]?.updateHighlight(true)
        }
        
        // Refresh the list view to show changes
        refresh()
    }
    
    fun clearSearchHighlights() {
        items.forEach { item ->
            item.updateHighlight(false)
        }
        refresh()
    }
}

class ThreadListItem(val pid: Int, val tid: Int) : TextFlow() {
    
    private val pidText = Text("PID: $pid, TID: $tid")
    private var isHighlighted = false
    
    init {
        pidText.styleClass += "perfscope-thread-list-item"
        children += pidText
        updateHighlight(false)
    }
    
    fun updateHighlight(highlighted: Boolean) {
        isHighlighted = highlighted
        if (highlighted) {
            // TODO use class
            style = "-fx-background-color: #ffeb3b; -fx-background-radius: 3; -fx-padding: 2 4;"
            pidText.fill = Color.BLACK
        } else {
            style = ""
            pidText.fill = Color.BLACK
        }
    }
    
    override fun toString(): String {
        return "PID: $pid, TID: $tid"
    }
}

class ThreadListCell : ListCell<ThreadListItem>() {
    
    override fun updateItem(item: ThreadListItem?, empty: Boolean) {
        super.updateItem(item, empty)

        if (empty || item == null) {
            graphic = null
        } else {
            graphic = item
        }
    }
}