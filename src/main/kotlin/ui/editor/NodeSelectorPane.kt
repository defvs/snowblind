package ui.editor

import helpers.AllNodes
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import nodes.INodeBase
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class NodeSelectorPane(private val compositorPane: NodeCompositorPane) : StackPane() {
    private sealed class ListItem : VBox() {
        class CategoryTitle(title: String, maxWidth: ReadOnlyDoubleProperty) : ListItem() {
            init {
                children += Label(title).apply {
                    font = Font(font.size * 1.1)
                    isUnderline = true
                    isWrapText = true
                    prefWidthProperty().bind(maxWidth.subtract(12))
                }
            }
        }

        class Node(private val forClass: KClass<out INodeBase>, maxWidth: ReadOnlyDoubleProperty) : ListItem() {
            init {
                val exampleNode = forClass.createInstance()
                this.children += Label(exampleNode.name).apply {
                    font = Font(font.size * 1.2)
                    isWrapText = true
                    prefWidthProperty().bind(maxWidth.subtract(12))
                }
                exampleNode.description?.let {
                    this.children += Label(it).apply {
                        font = Font(font.size * 0.9)
                        isWrapText = true
                        prefWidthProperty().bind(maxWidth.subtract(12))
                    }
                }
            }

            fun create(compositorPane: NodeCompositorPane) = forClass.createInstance().apply {
                position.first.set((compositorPane.width.toFloat() / 2).coerceAtLeast(0f))
                position.second.set((compositorPane.height.toFloat() / 2).coerceAtLeast(0f))
            }
        }
    }

    init {
        children += ListView<ListItem>().apply {
            items.apply {
                this += ListItem.CategoryTitle("Generator Nodes", widthProperty())
                this += AllNodes.GENERATOR_NODES.map { ListItem.Node(it, widthProperty()) }

                this += ListItem.CategoryTitle("Special Nodes", widthProperty())
                this += AllNodes.SPECIAL_NODES.map { ListItem.Node(it, widthProperty()) }

                this += ListItem.CategoryTitle("Transform Nodes", widthProperty())
                this += AllNodes.TRANSFORM_NODES.map { ListItem.Node(it, widthProperty()) }
            }
            setOnMouseClicked { event ->
                if (event.clickCount == 2) {
                    (selectionModel.selectedItem as? ListItem.Node)?.create(compositorPane)?.also { node ->
                        compositorPane.clip += node
                        compositorPane.addNode(node)
                    }
                }
            }
        }
    }
}
