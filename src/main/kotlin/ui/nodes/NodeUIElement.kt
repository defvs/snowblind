package ui.nodes

import helpers.ConnectorUUID
import helpers.Insets
import helpers.StackPane
import helpers.VBox
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import nodes.GeneratorNode
import nodes.INodeBase
import nodes.NodeParameter
import nodes.TransformNode
import nodes.implementations.special.InputNode
import nodes.implementations.special.OutputNode

class NodeUIElement(private val node: INodeBase) : VBox(), INodeBase by node {
    var onHeaderMousePressed: (e: MouseEvent) -> Unit = {}
    var onHeaderMouseDragged: (e: MouseEvent) -> Unit = {}

    private val ioCircles: List<NodeUIElementCircle>

    init {
        val ioCircles = arrayListOf<NodeUIElementCircle>()

        layoutXProperty().bindBidirectional(node.position.first)
        layoutYProperty().bindBidirectional(node.position.second)

        javaClass.getResource("/ui/nodeUIElement.css")?.toExternalForm()?.let { stylesheets += it }
        styleClass.add("node-ui-element")
        alignment = Pos.TOP_CENTER
        spacing = 0.0

        children += StackPane {
            alignment = Pos.CENTER
            isPickOnBounds = true // Makes the entire title container pickable
            id = IDs.NODE_HEADER_DRAGBOX
            padding = Insets(4.0, 8.0)
            setOnMousePressed { onHeaderMousePressed(it) }
            setOnMouseDragged { onHeaderMouseDragged(it) }

            if (node is TransformNode || node is OutputNode) {
                val laserInputUUID = (node as? TransformNode)?.laserInputUUID ?: (node as OutputNode).laserInputUUID
                NodeUIElementCircle(
                    5.0,
                    Color.RED,
                    laserInputUUID,
                    node.uuid,
                    ConnectorType.LaserInput
                ).apply {
                    StackPane.setAlignment(this, Pos.CENTER_LEFT)
                    translateX -= 8.0 + 5.0
                }.also {
                    children += it
                    ioCircles += it
                }
            }

            children += Label(name).apply {
                alignment = Pos.CENTER
                isMouseTransparent = true
            }

            if (node is TransformNode || node is InputNode || node is GeneratorNode) {
                val laserOutputUUID = when (node) {
                    is TransformNode -> node.laserOutputUUID
                    is InputNode -> node.laserOutputUUID
                    is GeneratorNode -> node.laserOutputUUID
                    else -> throw ClassCastException()
                }
                NodeUIElementCircle(
                    5.0,
                    Color.RED,
                    laserOutputUUID,
                    node.uuid,
                    ConnectorType.LaserOutput
                ).apply {
                    StackPane.setAlignment(this, Pos.CENTER_RIGHT)
                    translateX += 8.0 + 5.0
                }.also {
                    children += it
                    ioCircles += it
                }
            }
        }

        fun createParameterPane(parameter: NodeParameter) = StackPane {
            alignment = Pos.CENTER
            padding = Insets(4.0, 8.0)

            if (parameter is NodeParameter.ControllableParameter) children += parameter.initControl()

            when (parameter) {
                is NodeParameter.InputParameter,
                is NodeParameter.ControllableParameter.ControllableInputParameter,
                -> children.addFirst(
                    NodeUIElementCircle(
                        5.0,
                        Color.GREEN,
                        parameter.uuid,
                        node.uuid,
                        ConnectorType.ParameterInput
                    ).apply {
                        StackPane.setAlignment(this, Pos.CENTER_LEFT)
                        translateX -= 8.0 + 5.0
                    }.also { ioCircles += it }
                )

                is NodeParameter.OutputParameter -> children.addLast(
                    NodeUIElementCircle(
                        5.0,
                        Color.GREEN,
                        parameter.uuid,
                        node.uuid,
                        ConnectorType.ParameterOutput
                    ).apply {
                        StackPane.setAlignment(this, Pos.CENTER_RIGHT)
                        translateX += 8.0 + 5.0
                    }.also { ioCircles += it }
                )

                else -> Unit
            }
        }

        val parameterVBox = VBox {
            children.addAll(node.parameters.parameters.map(::createParameterPane))
        }

        children += parameterVBox
        this.ioCircles = ioCircles
    }

    fun getConnectorCircle(connectorUUID: ConnectorUUID) = ioCircles.first { it.connectorUUID == connectorUUID }
}

fun INodeBase.createUIElement() = NodeUIElement(this)
