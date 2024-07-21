package ui.nodes

import clips.Clip
import helpers.ConnectorUUID
import helpers.NodeUUID
import helpers.findParent
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.shape.Line
import nodes.INodeBase
import nodes.NodeConnection

class NodeCompositorPane(private val clip: Clip) : Pane() {
    private val connections = mutableListOf<LineConnector>()
    private val temporaryConnectionLine = Line().apply {
        isVisible = false
        strokeDashArray.add(16.0) // Dashed line
        isMouseTransparent = true
        viewOrder = -100.0
    }

    init {
        // Add clip nodes into view
        clip.nodes.values.forEach { addNode(it.createUIElement()) }
        // Add clip connections into view
        clip.connectionMap.connections.forEach { nodeConnection ->
            connections += createConnectionLine(nodeConnection).also { children.add(it) }
            connections += createConnectionLine(nodeConnection).also { children.add(it) }
        }
        children.add(temporaryConnectionLine)
        setOnMousePressed { onMousePressed(it) }
        setOnMouseDragged { onMouseDragged(it) }
        setOnMouseReleased { onMouseReleased(it) }
    }

    // region Node Header movement
    private object NodeDragContext {
        var initialX: Double = 0.0
        var initialY: Double = 0.0
        var offsetX: Double = 0.0
        var offsetY: Double = 0.0
    }

    private fun onNodeHeaderPressed(event: MouseEvent) {
        val node = event.target as? Node ?: return
        if (node.id == IDs.NodeHeaderDragbox) {
            val parentNodeUI = node.findParent<NodeUIElement>() ?: return
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
        val node = event.target as? Node ?: return
        if (node.id == IDs.NodeHeaderDragbox) {
            val parentNodeUI = node.findParent<NodeUIElement>() ?: return
            parentNodeUI.layoutX = NodeDragContext.initialX + event.sceneX - NodeDragContext.offsetX
            parentNodeUI.layoutY = NodeDragContext.initialY + event.sceneY - NodeDragContext.offsetY
        }
    }
    // endregion

    // region Mouse events
    private object NodeConnectorDragContext {
        var sourceNode: NodeUUID? = null
        var sourceConnector: NodeUIElementCircle? = null

        fun reset() {
            sourceNode = null
            sourceConnector = null
        }
    }

    private fun onMousePressed(event: MouseEvent) {
        when (val node = event.pickResult.intersectedNode) {
            is NodeUIElementCircle -> {
                var newNode = node
                if (node.connectorType.isInput && connections.any { node.connectorUUID in it.connection }) {
                    val line = connections.first { node.connectorUUID in it.connection }
                    disconnectNodes(line)
                    newNode = line.sourceConnector
                }
                NodeConnectorDragContext.sourceNode = newNode.parentNodeUUID
                NodeConnectorDragContext.sourceConnector = newNode
                temporaryConnectionLine.apply {
                    isVisible = true
                    newNode.localToScene(newNode.centerX, newNode.centerY).let {
                        startX = it.x
                        startY = it.y
                        endX = event.sceneX
                        endY = event.sceneY
                    }
                }
            }
            else -> return
        }
    }

    private fun onMouseDragged(event: MouseEvent) {
        if (NodeConnectorDragContext.sourceConnector != null) {
            temporaryConnectionLine.apply {
                endX = event.sceneX
                endY = event.sceneY
            }
        }
    }

    private fun onMouseReleased(event: MouseEvent) {
        temporaryConnectionLine.isVisible = false
        when (val node = event.pickResult.intersectedNode) {
            is NodeUIElementCircle -> {
                NodeConnectorDragContext.sourceNode ?: return
                NodeConnectorDragContext.sourceConnector ?: return
                if (NodeConnectorDragContext.sourceNode == node.parentNodeUUID) return
                if (NodeConnectorDragContext.sourceConnector!!.connectorUUID == node.connectorUUID) return
                if (NodeConnectorDragContext.sourceConnector!!.connectorType.opposite != node.connectorType) return

                connectNodes(
                    if (!NodeConnectorDragContext.sourceConnector!!.connectorType.isInput)
                        NodeConnection(
                            NodeConnectorDragContext.sourceNode!!,
                            NodeConnectorDragContext.sourceConnector!!.connectorUUID,
                            node.parentNodeUUID,
                            node.connectorUUID
                        )
                    else
                        NodeConnection(
                            node.parentNodeUUID,
                            node.connectorUUID,
                            NodeConnectorDragContext.sourceNode!!,
                            NodeConnectorDragContext.sourceConnector!!.connectorUUID
                        )
                )
            }

            else -> {}
        }
        NodeConnectorDragContext.reset()
    }

    // endregion

    // region Node Connection functions
    private fun connectNodes(nodeConnection: NodeConnection) {
        createConnectionLine(nodeConnection).let {
            children += it
            connections += it
        }
        clip.connectionMap += nodeConnection
    }

    private fun disconnectNodes(line: LineConnector) {
        children -= line
        connections -= line
        clip.connectionMap.removeConnection(line.connection)
    }

    private fun disconnectNodes(connectorUUID: ConnectorUUID) =
        disconnectNodes(connections.first { connectorUUID in it.connection })

    private fun createConnectionLine(connection: NodeConnection): LineConnector {
        val sourceConnector = getNode(connection.source.nodeUUID)!!.getConnectorCircle(connection.source.connectorUUID)
        val destinationConnector = getNode(connection.dest.nodeUUID)!!.getConnectorCircle(connection.dest.connectorUUID)
        return LineConnector(sourceConnector, destinationConnector, connection)
    }
    // endregion

    // region Node functions
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

    private fun getNode(uuid: NodeUUID) = this.children.find { (it as? NodeUIElement)?.uuid == uuid } as? NodeUIElement
    // endregion
}

private fun <E> MutableList<E>.front(node: E) {
    this.remove(node)
    this.add(node)
}
