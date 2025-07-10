package com.perfscope.view

import javafx.scene.control.TreeView
import com.perfscope.model.CallTreeData
import com.perfscope.db.DatabaseLoader
import javafx.scene.control.TreeItem
import javafx.scene.input.KeyCode
import javafx.scene.input.ClipboardContent
import javafx.beans.value.ObservableValue
import javafx.scene.input.KeyEvent
import javafx.util.Callback
import java.sql.SQLException
import kotlin.time.Duration

class CallTreeView(val dbPath: String) : TreeView<CallTreeData?>() {
    private val dbLoaderK = DatabaseLoader(dbPath)

    init {
        root = TreeItem<CallTreeData?>(CallTreeData.stub("Call Tree"))
        setShowRoot(false)
        cellFactory = Callback { tv: TreeView<CallTreeData?>? -> CallTreeCell() }
        onKeyPressed = javafx.event.EventHandler { event: KeyEvent -> this.handleKeyPress(event) }
    }

    private fun handleKeyPress(event: KeyEvent) {
        if (event.isControlDown() && event.getCode() == KeyCode.C) {
            copySelectedNodeToClipboard()
            event.consume()
        }
    }

    private fun copySelectedNodeToClipboard() {
        val selectedItem: TreeItem<CallTreeData?> = selectionModel.selectedItem
        if (selectedItem != null) {
            val data: CallTreeData? = selectedItem.getValue()
            val textToCopy: String?

            if (data!!.totalTime != null) {
                textToCopy = String.format(
                    "%s [%s]",
                    data!!.name,
                    com.perfscope.util.Duration.ofNanos(data!!.totalTime!!)
                )
            } else {
                textToCopy = data!!.name
            }

            val clipboard: javafx.scene.input.Clipboard = javafx.scene.input.Clipboard.getSystemClipboard()
            val content = ClipboardContent()
            content.putString(textToCopy)
            clipboard.setContent(content)

            logger.debug("Copied to clipboard: {}", textToCopy)
        }
    }

    fun loadThreadData(commId: Long, threadId: Long) {
        setRoot(TreeItem<CallTreeData?>(CallTreeData.stub("Call Tree")))
        setShowRoot(false)

        val totalTime: Duration = dbLoaderK.calcTotalTime(commId, threadId)
        loadCallTreeNodes(dbPath, commId, threadId, 1L, getRoot(), totalTime)
    }

    private fun loadCallTreeNodes(
        databasePath: String?, commId: Long?, threadId: Long, parentCallPathId: Long?,
        parentItem: TreeItem<CallTreeData?>, totalTime: Duration
    ) {
        try {
            val callTreeData: CallTreeData? = parentItem.getValue()
            val nodes: List<CallTreeData> = dbLoaderK.fetchCalls(
                commId!!,
                threadId,
                parentCallPathId!!,
                callTreeData!!.callTime,
                callTreeData!!.returnTime
            )

            for (nodeData in nodes) {
                nodeData.totalThreadTime = totalTime // TODO rename to totalThreadTime

                val item = TreeItem<CallTreeData?>(nodeData)

                // Add a dummy child to show expand arrow (will be replaced when expanded)
                item.getChildren()
                    .add(TreeItem<CallTreeData?>(CallTreeData.stub(DUMMY_LOADING_NODE_NAME)))

                item.expandedProperty()
                    .addListener(javafx.beans.value.ChangeListener { observable: ObservableValue<out Boolean?>?, oldValue: Boolean, newValue: Boolean ->
                        if (newValue && item.getChildren().size == 1 &&
                            item.getChildren().get(0).getValue()!!.name == DUMMY_LOADING_NODE_NAME
                        ) {
                            item.getChildren().clear()

                            loadCallTreeNodes(
                                databasePath,
                                commId,
                                threadId,
                                item.getValue()!!.callPathId,
                                item,
                                totalTime
                            )

                            if (item.getChildren().isEmpty()) {
                                item.getChildren().add(TreeItem<CallTreeData?>(CallTreeData.stub("(No calls)")))
                            }
                        }
                    })

                parentItem.getChildren().add(item)
            }
        } catch (e: SQLException) {
            logger.error("Error loading call tree nodes: {}", e.message, e)

            parentItem.getChildren().add(TreeItem<CallTreeData?>(CallTreeData.stub("Error loading data: " + e.message)))
        }
    }

    companion object {
        private val logger: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(CallTreeView::class.java)
        private const val DUMMY_LOADING_NODE_NAME: kotlin.String = "Loading..."
    }
}