package ui.nodes

import helpers.ConnectorUUID
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import nodes.*

class NodeUIElement(private val node: INodeBase) : VBox(), INodeBase by node {
    data class NodeIO(
        val name: String,
        val uuid: ConnectorUUID,
    )

    init {
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
        }

        val inputs: List<NodeIO> = buildList {
            (node as? INodeHasInputLaser)?.let {
                add(NodeIO("Laser", node.laserInputUUID))
            }

            (node as? INodeHasInputParams)?.let {
                addAll(it.inputParams.parameters.map { param ->
                    NodeIO(param.type.readableName, param.uuid)
                })
            }
        }

        val outputs: List<NodeIO> = buildList {
            (node as? INodeHasOutputLaser)?.let {
                add(NodeIO("Laser", node.laserOutputUUID))
            }

            (node as? INodeHasOutputParams)?.let {
                addAll(it.outputParams.parameters.map { param ->
                    NodeIO(param.type.readableName, param.uuid)
                })
            }
        }

        val inputColumn = VBox(10.0)
        inputs.forEach { (name, _) ->
            val inputItem = HBox(10.0)
            val circle = Circle(5.0, Color.RED)
            val nameLabel = Label(name)
            inputItem.children.addAll(circle, nameLabel)
            inputColumn.children.add(inputItem)
        }

        val outputColumn = VBox(10.0)
        outputs.forEach { (name, _) ->
            val outputItem = HBox(10.0)
            val nameLabel = Label(name)
            val circle = Circle(5.0, Color.GREEN)
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
    }
}

fun INodeBase.createUIElement() = NodeUIElement(this)
