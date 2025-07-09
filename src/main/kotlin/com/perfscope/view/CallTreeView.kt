package com.perfscope.view

import com.perfscope.model.CallTreeData.Companion.stub
import com.perfscope.util.Duration.Companion.ofNanos
import javafx.scene.control.TreeView
import com.perfscope.model.CallTreeData
import com.perfscope.db.DatabaseLoader
import javafx.scene.control.TreeItem
import com.perfscope.view.CallTreeCell
import javafx.scene.input.KeyCode
import javafx.scene.input.ClipboardContent
import com.perfscope.view.CallTreeView
import javafx.beans.value.ObservableValue
import java.sql.SQLException

class CallTreeView : TreeView<CallTreeData?>() {
    private val databaseLoader: DatabaseLoader = DatabaseLoader()

    init {
        setRoot(TreeItem<CallTreeData?>(CallTreeData.stub("Call Tree")))
        setShowRoot(false)
        setCellFactory(javafx.util.Callback { tv: TreeView<CallTreeData?>? -> CallTreeCell() })
        setOnKeyPressed(javafx.event.EventHandler { event: javafx.scene.input.KeyEvent -> this.handleKeyPress(event) })
    }

    private fun handleKeyPress(event: javafx.scene.input.KeyEvent) {
        if (event.isControlDown() && event.getCode() == KeyCode.C) {
            copySelectedNodeToClipboard()
            event.consume()
        }
    }

    private fun copySelectedNodeToClipboard() {
        val selectedItem: TreeItem<CallTreeData?> = getSelectionModel().getSelectedItem()
        if (selectedItem != null) {
            val data: CallTreeData? = selectedItem.getValue()
            val textToCopy: String?

            if (data!!.totalTime != null) {
                textToCopy = kotlin.String.format(
                    "%s [%s]",
                    data!!.name,
                    com.perfscope.util.Duration.ofNanos(data!!.totalTime!!)
                )
            } else {
                textToCopy = data!!.name
            }

            val clipboard: javafx.scene.input.Clipboard = javafx.scene.input.Clipboard.getSystemClipboard()
            val content: ClipboardContent = ClipboardContent()
            content.putString(textToCopy)
            clipboard.setContent(content)

            CallTreeView.Companion.logger.debug("Copied to clipboard: {}", textToCopy)
        }
    }

    fun loadThreadData(databasePath: kotlin.String?, commId: Long?, threadId: Long?) {
        setRoot(TreeItem<CallTreeData?>(CallTreeData.stub("Call Tree")))
        setShowRoot(false)

        val totalTimeNanos: Long = databaseLoader.calculateTotalTimeNanos(databasePath, commId, threadId)
        loadCallTreeNodes(databasePath, commId, threadId, 1L, getRoot(), totalTimeNanos)
    }

    private fun loadCallTreeNodes(
        databasePath: kotlin.String?, commId: Long?, threadId: Long?, parentCallPathId: Long?,
        parentItem: TreeItem<CallTreeData?>, totalTimeNanos: Long
    ) {
        try {
            val callTreeData: CallTreeData? = parentItem.getValue()
            val nodes: kotlin.collections.MutableList<CallTreeData> = databaseLoader.loadCallTreeNodes(
                databasePath,
                commId,
                threadId,
                parentCallPathId,
                callTreeData!!.callTime,
                callTreeData!!.returnTime
            )

            for (nodeData in nodes) {
                nodeData.setTotalTimeNanos(totalTimeNanos)

                val item = TreeItem<CallTreeData?>(nodeData)

                // Add a dummy child to show expand arrow (will be replaced when expanded)
                item.getChildren()
                    .add(TreeItem<CallTreeData?>(CallTreeData.stub(CallTreeView.Companion.DUMMY_LOADING_NODE_NAME)))

                item.expandedProperty()
                    .addListener(javafx.beans.value.ChangeListener { observable: ObservableValue<out Boolean?>?, oldValue: Boolean, newValue: Boolean ->
                        if (newValue && item.getChildren().size == 1 &&
                            item.getChildren().get(0).getValue()!!.name == CallTreeView.Companion.DUMMY_LOADING_NODE_NAME
                        ) {
                            item.getChildren().clear()

                            loadCallTreeNodes(
                                databasePath,
                                commId,
                                threadId,
                                item.getValue()!!.callPathId,
                                item,
                                totalTimeNanos
                            )

                            if (item.getChildren().isEmpty()) {
                                item.getChildren().add(TreeItem<CallTreeData?>(CallTreeData.stub("(No calls)")))
                            }
                        }
                    })

                parentItem.getChildren().add(item)
            }
        } catch (e: SQLException) {
            CallTreeView.Companion.logger.error("Error loading call tree nodes: {}", e.message, e)

            parentItem.getChildren().add(TreeItem<CallTreeData?>(CallTreeData.stub("Error loading data: " + e.message)))
        }
    }

    companion object {
        private val logger: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(CallTreeView::class.java)
        private const val DUMMY_LOADING_NODE_NAME: kotlin.String = "Loading..."
    }
}