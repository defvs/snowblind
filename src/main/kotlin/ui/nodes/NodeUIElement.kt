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
import nodes.*
import ui.nodes.controls.NodeParameterControl

class NodeUIElement(private val node: INodeBase) : VBox(), INodeBase by node {
    var onHeaderMousePressed: (e: MouseEvent) -> Unit = {}
    var onHeaderMouseDragged: (e: MouseEvent) -> Unit = {}

    private val ioCircles: List<NodeUIElementCircle>
    private val parameters: List<NodeParameterMap>

    data class NodeIO(
        val name: String,
        val uuid: ConnectorUUID,
        val connectorType: ConnectorType,
    )

    init {
        val ioCircles = arrayListOf<NodeUIElementCircle>()

        this.javaClass.getResource("/ui/nodeUIElement.css")?.toExternalForm()?.let { stylesheets += it }
        this.styleClass.add("node-ui-element")
        this.alignment = Pos.TOP_CENTER
        this.spacing = 0.0

        this.children += StackPane {
            alignment = Pos.CENTER
            isPickOnBounds = true // Makes the entire title container pickable
            id = IDs.NodeHeaderDragbox
            padding = Insets(4.0, 8.0)
            setOnMousePressed { onHeaderMousePressed(it) }
            setOnMouseDragged { onHeaderMouseDragged(it) }

            if (node is INodeHasInputLaser) {
                NodeUIElementCircle(
                    5.0,
                    Color.RED,
                    node.laserInputUUID,
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

            if (node is INodeHasOutputLaser) {
                NodeUIElementCircle(
                    5.0,
                    Color.RED,
                    node.laserOutputUUID,
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

        fun createParameterPane(
            parameter: Triple<NodeParameterDefinition, NodeParameter, NodeParameterControl>,
            type: ConnectorType?,
        ) = parameter.let { (definition, param, control) ->
            StackPane {
                alignment = Pos.CENTER
                padding = Insets(4.0, 8.0)

                if (type == ConnectorType.ParameterInput)
                    children += NodeUIElementCircle(5.0, Color.GREEN, param.uuid, node.uuid, type)
                        .apply {
                            StackPane.setAlignment(this, Pos.CENTER_LEFT)
                            translateX -= 8.0 + 5.0
                        }
                        .also { ioCircles += it }
                children += control.createControl(param.data, definition)
                if (type == ConnectorType.ParameterOutput)
                    children += NodeUIElementCircle(5.0, Color.GREEN, param.uuid, node.uuid, type)
                        .apply {
                            StackPane.setAlignment(this, Pos.CENTER_RIGHT)
                            translateX += 8.0 + 5.0
                        }
                        .also { ioCircles += it }
            }
        }

        val parameterHBox = VBox {}

        parameters = listOfNotNull(
            (node as? INodeHasInternalParams)?.internalParams
                ?.also {
                    it.flatten().map { param -> parameterHBox.children += createParameterPane(param, null) }
                },
            (node as? INodeHasInputParams)?.inputParams
                ?.also {
                    it.flatten().map { param ->
                        parameterHBox.children += createParameterPane(
                            param,
                            ConnectorType.ParameterInput
                        )
                    }
                },
            (node as? INodeHasOutputParams)?.outputParams
                ?.also {
                    it.flatten().map { param ->
                        parameterHBox.children += createParameterPane(
                            param,
                            ConnectorType.ParameterOutput
                        )
                    }
                },
        )

        this.children += parameterHBox
        this.ioCircles = ioCircles
    }

    fun getConnectorCircle(connectorUUID: ConnectorUUID) = ioCircles.first { it.connectorUUID == connectorUUID }
}

fun INodeBase.createUIElement() = NodeUIElement(this)
