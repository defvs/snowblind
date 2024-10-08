package ui.editor

import clips.Clip
import helpers.ConnectorUUID
import helpers.NodeUUID
import helpers.findParent
import helpers.front
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.MapChangeListener
import javafx.collections.SetChangeListener
import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.input.Dragboard
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.shape.Line
import nodes.INodeBase
import nodes.NodeConnection
import nodes.NodeParameter
import ui.nodes.*
import kotlin.reflect.full.createInstance

class NodeCompositorPane(val clip: Clip) : Pane() {
    private val connections = hashSetOf<LineConnector>()
    private val temporaryConnectionLine = Line().apply {
        isVisible = false
        strokeDashArray.add(16.0) // Dashed line
        isMouseTransparent = true
        viewOrder = -100.0
    }

    val hasUnsavedChanges = SimpleBooleanProperty(false)

    init {
        // Styling
        style = """
            -fx-background-color: #ffffff,
            linear-gradient(from 0.5px 0.0px to 15.5px  0.0px, repeat, #00000008 5%, transparent 5%),
            linear-gradient(from 0.0px 0.5px to  0.0px 15.5px, repeat, #00000008 5%, transparent 5%);   
        """

        // Setup binding from clip.nodes
        clip.nodes.addListener { change: MapChangeListener.Change<out NodeUUID, out INodeBase> ->
            when {
                change.wasAdded() -> addNode(change.valueAdded)
                change.wasRemoved() -> removeNode(change.key)
            }
            hasUnsavedChanges.set(true)
        }
        // Initialize
        clip.nodes.forEach { (_, value) ->
            addNode(value)
        }

        // Setup binding from clip.connectionMap
        clip.connectionMap.addListener { change: SetChangeListener.Change<out NodeConnection> ->
            when {
                change.wasAdded() -> {
                    connections.add(LineConnector(change.elementAdded, this).also { children += it })
                    getNode(change.elementAdded.dest.nodeUUID)?.parameters?.get(change.elementAdded.dest.connectorUUID)
                        ?.let {
                            it as? NodeParameter.ControllableParameter.ControllableInputParameter
                        }?.control?.isConnected?.set(true)
                }

                change.wasRemoved() -> {
                    connections.removeIf { it.connection == change.elementRemoved }
                    children.removeIf { it is LineConnector && it.connection == change.elementRemoved }
                    getNode(change.elementRemoved.dest.nodeUUID)?.parameters?.get(change.elementRemoved.dest.connectorUUID)
                        ?.let {
                            it as? NodeParameter.ControllableParameter.ControllableInputParameter
                        }?.control?.isConnected?.set(false)
                }
            }
            hasUnsavedChanges.set(true)
        }
        // Initialize
        clip.connectionMap.forEach { value ->
            connections.add(LineConnector(value, this).also { children += it })
        }

        children.add(temporaryConnectionLine)
        setOnMousePressed { onMousePressed(it) }
        setOnMouseDragged { onMouseDragged(it) }
        setOnMouseReleased { onMouseReleased(it) }

        setOnDragOver { event ->
            if (event.gestureSource != this && event.dragboard.hasString()) {
                event.acceptTransferModes(TransferMode.COPY)
            }
            event.consume()
        }

        setOnDragDropped { event ->
            val dragboard: Dragboard = event.dragboard
            var success = false
            if (dragboard.hasString()) {
                // Retrieve the node's class by its qualified name
                val node = run {
                    val nodeClass = Class.forName(dragboard.string).kotlin
                    nodeClass.createInstance() as? INodeBase
                } ?: return@setOnDragDropped

                // Set the node's position to the drop location
                node.position.first.set(event.x.toFloat().coerceAtLeast(0f))
                node.position.second.set(event.y.toFloat().coerceAtLeast(0f))

                // Add the node to the compositor
                addNode(node)

                success = true
            }
            event.isDropCompleted = success
            event.consume()
        }
    }

    // region Node Header movement
    private object NodeDragContext {
        var initialX: Double = 0.0
        var initialY: Double = 0.0
        var offsetX: Double = 0.0
        var offsetY: Double = 0.0
    }

    private fun onNodeHeaderPressed(event: MouseEvent) {
        if (event.button != MouseButton.PRIMARY) return
        val node = event.target as? Node ?: return
        if (node.id == IDs.NODE_HEADER_DRAGBOX) {
            val parentNodeUI = node.findParent<NodeUIElement>() ?: return
            prefWidth = width
            prefHeight = height
            this.children.front(parentNodeUI)
            NodeDragContext.apply {
                initialX = parentNodeUI.layoutX
                initialY = parentNodeUI.layoutY
                offsetX = event.sceneX
                offsetY = event.sceneY
            }

            event.consume()
        }
    }

    private fun onNodeHeaderDragged(event: MouseEvent) {

        if (event.button != MouseButton.PRIMARY) return
        val node = event.target as? Node ?: return
        if (node.id == IDs.NODE_HEADER_DRAGBOX) {
            val parentNodeUI = node.findParent<NodeUIElement>() ?: return
            parentNodeUI.layoutX = (NodeDragContext.initialX + event.sceneX - NodeDragContext.offsetX)
                .coerceAtLeast(0.0)
            parentNodeUI.layoutY = (NodeDragContext.initialY + event.sceneY - NodeDragContext.offsetY)
                .coerceAtLeast(0.0)

            event.consume()
        }
    }

    private fun onNodeHeaderReleased(event: MouseEvent) {
        if (event.button != MouseButton.PRIMARY) return
        val node = event.target as? Node ?: return
        if (node.id == IDs.NODE_HEADER_DRAGBOX) {
            prefWidth = Region.USE_COMPUTED_SIZE
            prefHeight = Region.USE_COMPUTED_SIZE
            event.consume()
        }
    }
    // endregion

    // region Connectors connection logic
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
                    val start =
                        this@NodeCompositorPane.sceneToLocal(newNode.localToScene(newNode.centerX, newNode.centerY))
                    startX = start.x
                    startY = start.y
                    val end = this@NodeCompositorPane.sceneToLocal(event.sceneX, event.sceneY)
                    endX = end.x
                    endY = end.y
                }
            }

            else -> return
        }
    }

    private fun onMouseDragged(event: MouseEvent) {
        if (NodeConnectorDragContext.sourceConnector != null) {
            temporaryConnectionLine.apply {
                val end = this@NodeCompositorPane.sceneToLocal(event.sceneX, event.sceneY)
                endX = end.x
                endY = end.y
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

                // Check if ParameterInput and dest is already in the map
                val isInvalidParameterInput = let {
                    val sourceInvalid =
                        (NodeConnectorDragContext.sourceConnector!!.connectorType == ConnectorType.ParameterInput)
                            .and(NodeConnectorDragContext.sourceConnector!!.connectorUUID in clip.connectionMap)
                    val destInvalid =
                        (node.connectorType == ConnectorType.ParameterInput)
                            .and(node.connectorUUID in clip.connectionMap)

                    sourceInvalid || destInvalid
                }
                if (isInvalidParameterInput) return

                clip.connectionMap +=
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
            }

            else -> {}
        }
        NodeConnectorDragContext.reset()
    }

    // endregion

    // region Node Connection functions
    private fun disconnectNodes(line: LineConnector) = clip.connectionMap.remove(line.connection)

    private fun disconnectNodes(connectorUUID: ConnectorUUID) =
        disconnectNodes(connections.first { connectorUUID in it.connection })

    private fun disconnectNodes(nodeUUID: NodeUUID) =
        connections.firstOrNull { nodeUUID in it.connection }?.let { disconnectNodes(it) }
    // endregion

    // region Node functions
    fun addNode(node: INodeBase) {
        node.createUIElement().apply {
            onHeaderMousePressed = ::onNodeHeaderPressed
            onHeaderMouseDragged = ::onNodeHeaderDragged
            onHeaderMouseReleased = ::onNodeHeaderReleased

            // Set up context menu on node header
            val contextMenu = ContextMenu()
            val deleteItem = MenuItem("Delete")
            deleteItem.setOnAction {
                this@NodeCompositorPane.clip.nodes.remove(this.uuid)
                this@NodeCompositorPane.clip.connectionMap.remove(this.uuid)
            }
            contextMenu.items.add(deleteItem)
            this.children.single { it.id == IDs.NODE_HEADER_DRAGBOX }
                .setOnContextMenuRequested { event ->
                    contextMenu.show(this, event.screenX, event.screenY)
                }
        }.let { this.children.add(it) }
    }

    fun removeNode(uuid: NodeUUID) {
        val nodeToRemove = getNode(uuid) ?: return
        this.children.remove(nodeToRemove)
    }

    fun getNode(uuid: NodeUUID) = this.children.find { (it as? NodeUIElement)?.uuid == uuid } as? NodeUIElement

    // endregion
}