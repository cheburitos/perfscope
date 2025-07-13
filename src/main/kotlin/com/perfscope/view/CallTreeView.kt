package com.perfscope.view

import javafx.scene.control.TreeView
import com.perfscope.model.Call
import com.perfscope.db.DatabaseLoader
import javafx.scene.control.TreeItem
import javafx.scene.input.KeyCode
import javafx.scene.input.ClipboardContent
import javafx.beans.value.ObservableValue
import javafx.scene.input.Clipboard
import javafx.scene.input.KeyEvent
import javafx.util.Callback
import java.sql.SQLException
import kotlin.time.Duration

class CallTreeView(val dbPath: String) : TreeView<Call?>() {
    private val dbLoader = DatabaseLoader(dbPath)

    init {
        root = TreeItem<Call?>(Call.stub("Call Tree"))
        setShowRoot(false)
        cellFactory = Callback { tv: TreeView<Call?>? -> CallTreeCell() }
        onKeyPressed = javafx.event.EventHandler { event: KeyEvent -> this.handleKeyPress(event) }
    }

    private fun handleKeyPress(event: KeyEvent) {
        if (event.isControlDown && event.code == KeyCode.C) {
            copySelectedNodeToClipboard()
            event.consume()
        }
    }

    private fun copySelectedNodeToClipboard() {
        val selectedItem: TreeItem<Call?> = selectionModel.selectedItem
        val selectedCall: Call? = selectedItem.getValue()
        if (selectedCall == null) {
            return
        }
        val textToCopy: String?

        if (selectedCall.totalTime != null) {
            textToCopy = String.format(
                "%s [%s]",
                selectedCall.name,
                com.perfscope.util.Duration.ofNanos(selectedCall.totalTime!!) //TODO some other func
            )
        } else {
            textToCopy = selectedCall.name
        }

        val content = ClipboardContent()
        content.putString(textToCopy)
        val clipboard = Clipboard.getSystemClipboard()
        clipboard.setContent(content)
        logger.debug("Copied to clipboard: {}", textToCopy)
    }

    fun switchThread(commandId: Long, threadId: Long) {
        root = TreeItem<Call?>(Call.stub("Call Tree"))
        isShowRoot = false

        val totalTime: Duration = dbLoader.calcTotalTime(commandId, threadId)
        loadChildrenCalls(dbPath, commandId, threadId, 1L, root, totalTime)
    }

    private fun loadChildrenCalls(
        dbPath: String,
        commandId: Long,
        threadId: Long,
        parentCallPathId: Long,
        parentItem: TreeItem<Call?>,
        totalTime: Duration
    ) {
        try {
            val call: Call? = parentItem.getValue()
            if (call == null) {
                return
            }
            val childrenCalls: List<Call> = dbLoader.fetchCalls(
                commandId,
                threadId,
                parentCallPathId,
                call.callTime,
                call.returnTime
            )

            for (childCall in childrenCalls) {
                childCall.totalThreadTime = totalTime // TODO rename to totalThreadTime

                val item = TreeItem(childCall)

                // Add a dummy child to show expand arrow (will be replaced when expanded)
                item.children += TreeItem(Call.stub(DUMMY_LOADING_NODE_NAME))

                item.expandedProperty().addListener { _: ObservableValue<out Boolean?>?, oldValue: Boolean, newValue: Boolean ->
                    if (newValue && item.children.size == 1 && item.children[0].value!!.name == DUMMY_LOADING_NODE_NAME) {
                        item.children.clear()

                        loadChildrenCalls(
                            dbPath,
                            commandId,
                            threadId,
                            item.value!!.callPathId!!,
                            item,
                            totalTime
                        )

                        if (item.children.isEmpty()) {
                            item.children += TreeItem(Call.stub("(No calls)"))
                        }
                    }
                }

                parentItem.children += item
            }
        } catch (e: SQLException) {
            logger.error("Error loading call tree nodes: {}", e.message, e)

            parentItem.getChildren().add(TreeItem<Call?>(Call.stub("Error loading data: " + e.message)))
        }
    }

    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(CallTreeView::class.java)
        private const val DUMMY_LOADING_NODE_NAME: String = "Loading..."
    }

}