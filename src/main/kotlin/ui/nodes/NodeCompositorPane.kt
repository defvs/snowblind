package ui.nodes

import clips.Clip
import helpers.ConnectorUUID
import helpers.NodeUUID
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import nodes.INodeBase

class NodeCompositorPane(private val clip: Clip) : Pane() {
    init {
        clip.nodes.values.forEach { addNode(it.createUIElement()) }
        setOnMousePressed { onMousePressed(it) }
        setOnMouseReleased { onMouseReleased(it) }
    }

    private fun onNodeHeaderPressed(event: MouseEvent) {
        val node = event.target as? HBox ?: return
        if (node.id == "dragbox") {
            val parentNodeUI = node.parent as? NodeUIElement ?: return
            this.children.front(parentNodeUI)
            NodeDragContext.apply {
                initialX = parentNodeUI.layoutX
                initialY = parentNodeUI.layoutY
                offsetX = event.sceneX
                offsetY = event.sceneY
            }
        }
    }

    private fun onNodeHeaderDragged(event: MouseEvent) {
        val node = event.target as? HBox ?: return
        if (node.id == "dragbox") {
            val parentNodeUI = node.parent as? NodeUIElement ?: return
            parentNodeUI.layoutX = NodeDragContext.initialX + event.sceneX - NodeDragContext.offsetX
            parentNodeUI.layoutY = NodeDragContext.initialY + event.sceneY - NodeDragContext.offsetY
        }
    }

    private fun onMousePressed(event: MouseEvent) {
        when (val node = event.pickResult.intersectedNode) {
            is NodeUIElementCircle -> {
                NodeConnectorDragContext.sourceNode = node.parentNodeUUID
                NodeConnectorDragContext.sourceConnector = node.connectorUUID
                println("Start drag from ${node.connectorUUID}.")
            }
            else -> return
        }
    }

    private fun onMouseReleased(event: MouseEvent) {
        when (val node = event.pickResult.intersectedNode) {
            is NodeUIElementCircle -> {
                if (NodeConnectorDragContext.sourceNode == node.parentNodeUUID) return
                if (NodeConnectorDragContext.sourceConnector == node.connectorUUID) return
                println("Connected ${NodeConnectorDragContext.sourceConnector} to ${node.connectorUUID}.")
            }
            else -> {}
        }
        NodeConnectorDragContext.reset()
    }

    fun addNode(node: INodeBase) {
        clip += node
        node.createUIElement().apply {
            onHeaderMousePressed = ::onNodeHeaderPressed
            onHeaderMouseDragged = ::onNodeHeaderDragged
        }.let { this.children.add(it) }
    }

    fun removeNode(uuid: NodeUUID) {
        clip.nodes.remove(uuid)
        this.children.removeIf { (it as? NodeUIElement)?.uuid == uuid }
    }

    private object NodeDragContext {
        var initialX: Double = 0.0
        var initialY: Double = 0.0
        var offsetX: Double = 0.0
        var offsetY: Double = 0.0
    }

    private object NodeConnectorDragContext {
        var sourceNode: NodeUUID? = null
        var sourceConnector: ConnectorUUID? = null

        fun reset() {
            sourceNode = null
            sourceConnector = null
        }
    }
}

private fun <E> MutableList<E>.front(node: E) {
    this.remove(node)
    this.add(node)
}
