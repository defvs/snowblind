package ui.editor

import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.scene.control.Label
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import nodes.INodeBase
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class NodeSelectorPane(private val editorPane: EditorPane) : StackPane() {

    private sealed class TreeNodeItem : VBox() {
        class NodeTitle(title: String, maxWidth: ReadOnlyDoubleProperty) : TreeNodeItem() {
            init {
                children += Label(title).apply {
                    font = Font(font.size * 1.1)
                    isUnderline = true
                    isWrapText = true
                    prefWidthProperty().bind(maxWidth.subtract(48))
                }
            }
        }

        class Node(val forClass: KClass<out INodeBase>, maxWidth: ReadOnlyDoubleProperty) : TreeNodeItem() {
            init {
                val exampleNode = forClass.createInstance()
                this.children += Label(exampleNode.name).apply {
                    font = Font(font.size * 1.2)
                    isWrapText = true
                    prefWidthProperty().bind(maxWidth.subtract(48))
                }
                exampleNode.description?.let {
                    this.children += Label(it).apply {
                        font = Font(font.size * 0.9)
                        isWrapText = true
                        prefWidthProperty().bind(maxWidth.subtract(48))
                    }
                }
            }

            fun create(compositorPane: NodeCompositorPane): INodeBase = forClass.createInstance().apply {
                position.first.set((compositorPane.width.toFloat() / 2).coerceAtLeast(0f))
                position.second.set((compositorPane.height.toFloat() / 2).coerceAtLeast(0f))
            }
        }
    }

    init {
        val rootItem = TreeItem<TreeNodeItem>()

        fun createCategoryTreeItem(
            categoryName: String,
            nodes: List<KClass<out INodeBase>>,
            maxWidth: ReadOnlyDoubleProperty,
        ): TreeItem<TreeNodeItem> {
            val categoryItem = TreeItem<TreeNodeItem>(TreeNodeItem.NodeTitle(categoryName, maxWidth))
            nodes.forEach {
                val nodeItem = TreeItem<TreeNodeItem>(TreeNodeItem.Node(it, maxWidth))
                categoryItem.children.add(nodeItem)

                // Set up drag-and-drop initiation
                nodeItem.value.setOnDragDetected { event ->
                    val content = ClipboardContent()
                    content.putString(it.qualifiedName)
                    startDragAndDrop(TransferMode.COPY).setContent(content)
                    event.consume()
                }
            }
            categoryItem.isExpanded = true
            return categoryItem
        }

        editorPane.nodeCompositor.clip.AVAILABLE_NODES.forEach { (category, items) ->
            if (items.isEmpty()) return@forEach
            rootItem.children.add(createCategoryTreeItem(category.categoryTitle, items, widthProperty()))
        }

        children += TreeView(rootItem).apply {
            isShowRoot = false  // This removes the display of the root item
            setOnMouseClicked { event ->
                if (event.clickCount == 2) {
                    (selectionModel.selectedItem?.value as? TreeNodeItem.Node)?.create(editorPane.nodeCompositor)?.also { node ->
                        editorPane.nodeCompositor.clip += node
                    }
                }
            }
        }
    }
}
