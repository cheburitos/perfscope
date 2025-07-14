package com.perfscope.view

import com.perfscope.model.Thread
import javafx.scene.control.ListView
import javafx.scene.text.Text
import javafx.scene.text.TextFlow

class ThreadListView : ListView<ThreadListItem>() {

    fun add(thread: Thread) {
        items += ThreadListItem(String.format("PID: %d, TID: %d", thread.pid, thread.tid))
    }
}

class ThreadListItem(val text: String) : TextFlow() {

    init {
        val textNode = Text(text)
        textNode.styleClass += "perfscope-thread-list-item"
        children += textNode
    }
}