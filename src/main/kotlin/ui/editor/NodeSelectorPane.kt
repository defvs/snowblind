package ui.editor

import helpers.AllNodes
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.scene.control.Label
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import nodes.INodeBase
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class NodeSelectorPane(private val compositorPane: NodeCompositorPane) : StackPane() {

    private sealed class TreeNodeItem : VBox() {
        class NodeTitle(title: String, maxWidth: ReadOnlyDoubleProperty) : TreeNodeItem() {
            init {
                children += Label(title).apply {
                    font = Font(font.size * 1.1)
                    isUnderline = true
                    isWrapText = true
                    prefWidthProperty().bind(maxWidth.subtract(24))  // Adjusted for padding/margin
                }
            }
        }

        class Node(private val forClass: KClass<out INodeBase>, maxWidth: ReadOnlyDoubleProperty) : TreeNodeItem() {
            init {
                val thisNode = forClass.createInstance()
                this.children += Label(thisNode.name).apply {
                    font = Font(font.size * 1.2)
                    isWrapText = true
                    prefWidthProperty().bind(maxWidth.subtract(48))  // Adjusted for padding/margin
                }
                thisNode.description?.let {
                    this.children += Label(it).apply {
                        font = Font(font.size * 0.9)
                        isWrapText = true
                        prefWidthProperty().bind(maxWidth.subtract(48))  // Adjusted for padding/margin
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
        // Create a root item that won't be shown
        val rootItem = TreeItem<TreeNodeItem>()

        // Function to create a TreeItem for each category with its respective nodes
        fun createCategoryTreeItem(categoryName: String, nodes: List<KClass<out INodeBase>>, maxWidth: ReadOnlyDoubleProperty): TreeItem<TreeNodeItem> {
            val categoryItem = TreeItem<TreeNodeItem>(TreeNodeItem.NodeTitle(categoryName, maxWidth))
            nodes.forEach {
                val nodeItem = TreeItem<TreeNodeItem>(TreeNodeItem.Node(it, maxWidth))
                categoryItem.children.add(nodeItem)
            }
            categoryItem.isExpanded = true
            return categoryItem
        }

        // Add categories to the root item
        rootItem.children.add(createCategoryTreeItem("Generator Nodes", AllNodes.GENERATOR_NODES, widthProperty()))
        rootItem.children.add(createCategoryTreeItem("Special Nodes", AllNodes.SPECIAL_NODES, widthProperty()))
        rootItem.children.add(createCategoryTreeItem("Transform Nodes", AllNodes.TRANSFORM_NODES, widthProperty()))

        // Create the TreeView, set showRoot to false, and add it to the StackPane
        children += TreeView(rootItem).apply {
            isShowRoot = false  // This removes the display of the root item
            setOnMouseClicked { event ->
                if (event.clickCount == 2) {
                    (selectionModel.selectedItem?.value as? TreeNodeItem.Node)?.create(compositorPane)?.also { node ->
                        compositorPane.clip += node
                    }
                }
            }
        }
    }
}
