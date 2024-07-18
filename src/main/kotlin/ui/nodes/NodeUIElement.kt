package ui.nodes

import helpers.ConnectorUUID
import helpers.NodeUUID
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Circle
import nodes.*

class NodeUIElementCircle(
    radius: Double,
    fill: Paint,
    val connectorUUID: ConnectorUUID,
    val parentNodeUUID: NodeUUID,
    val connectorType: ConnectorType,
) : Circle(radius, fill) {
    enum class ConnectorType(val isInput: Boolean) {
        LaserInput(true),
        LaserOutput(false),
        ParameterInput(true),
        ParameterOutput(false),
        ;

        val opposite: ConnectorType
            get() = when (this) {
                LaserInput -> LaserOutput
                LaserOutput -> LaserInput
                ParameterInput -> ParameterInput
                ParameterOutput -> ParameterOutput
            }
    }
}

class NodeUIElement(private val node: INodeBase) : VBox(), INodeBase by node {
    var onHeaderMousePressed: (e: MouseEvent) -> Unit = {}
    var onHeaderMouseDragged: (e: MouseEvent) -> Unit = {}

    private val ioCircles: List<NodeUIElementCircle>

    data class NodeIO(
        val name: String,
        val uuid: ConnectorUUID,
        val connectorType: NodeUIElementCircle.ConnectorType,
    )

    init {
        val ioCirclesBuilder = mutableListOf<NodeUIElementCircle>()

        this.javaClass.getResource("/ui/nodeUIElement.css")?.toExternalForm()?.let { stylesheets += it }
        styleClass.add("node-ui-element")

        val titleLabel = Label(name).apply {
            styleClass.add("title-label")
            alignment = Pos.CENTER
            isMouseTransparent = true
        }
        val titleContainer = HBox(titleLabel).apply {
            alignment = Pos.CENTER
            isPickOnBounds = true // Makes the entire title container pickable
            id = "dragbox"
            styleClass.add("title-container")
            setOnMousePressed { onHeaderMousePressed(it) }
            setOnMouseDragged { onHeaderMouseDragged(it) }
        }

        val inputs: List<NodeIO> = buildList {
            (node as? INodeHasInputLaser)?.let {
                add(NodeIO("Laser", node.laserInputUUID, NodeUIElementCircle.ConnectorType.LaserInput))
            }

            (node as? INodeHasInputParams)?.let {
                addAll(it.inputParams.parameters.map { param ->
                    NodeIO(param.type.readableName, param.uuid, NodeUIElementCircle.ConnectorType.ParameterInput)
                })
            }
        }

        val outputs: List<NodeIO> = buildList {
            (node as? INodeHasOutputLaser)?.let {
                add(NodeIO("Laser", node.laserOutputUUID, NodeUIElementCircle.ConnectorType.LaserOutput))
            }

            (node as? INodeHasOutputParams)?.let {
                addAll(it.outputParams.parameters.map { param ->
                    NodeIO(param.type.readableName, param.uuid, NodeUIElementCircle.ConnectorType.ParameterOutput)
                })
            }
        }

        val inputColumn = VBox(10.0)
        inputs.forEach { (name, uuid, connectorType) ->
            val inputItem = HBox(10.0)
            val circle = NodeUIElementCircle(5.0, Color.RED, uuid, this.uuid, connectorType)
            val nameLabel = Label(name)
            ioCirclesBuilder.add(circle)
            inputItem.children.addAll(circle, nameLabel)
            inputColumn.children.add(inputItem)
        }

        val outputColumn = VBox(10.0)
        outputs.forEach { (name, uuid, connectorType) ->
            val outputItem = HBox(10.0)
            val nameLabel = Label(name)
            val circle = NodeUIElementCircle(5.0, Color.GREEN, uuid, this.uuid, connectorType)
            ioCirclesBuilder.add(circle)
            outputItem.children.addAll(nameLabel, circle)
            outputColumn.children.add(outputItem)
        }

        val gridPane = GridPane()
        gridPane.add(inputColumn, 0, 0)
        gridPane.add(outputColumn, 1, 0)
        gridPane.hgap = 20.0
        gridPane.alignment = Pos.CENTER
        gridPane.padding = Insets(20.0)

        this.alignment = Pos.TOP_CENTER
        this.spacing = 20.0
        this.children.addAll(titleContainer, gridPane)

        ioCircles = ioCirclesBuilder
    }

    fun getConnectorCircle(connectorUUID: ConnectorUUID) = ioCircles.first { it.connectorUUID == connectorUUID }
}

fun INodeBase.createUIElement() = NodeUIElement(this)
