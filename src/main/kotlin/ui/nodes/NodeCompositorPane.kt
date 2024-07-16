package ui.nodes

import helpers.NodeUUID
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane

class NodeCompositorPane(vararg nodes: NodeUIElement) : Pane() {

    init {
        this.children.addAll(nodes)
        this.setOnMousePressed { event -> onMousePressed(event) }
        this.setOnMouseDragged { event -> onMouseDragged(event) }
    }

    private var dragContext = DragContext()

    private fun onMousePressed(event: MouseEvent) {
        val node = event.target as? HBox ?: return
        if (node.id == "dragbox") {
            val parentNodeUI = node.parent as? NodeUIElement ?: return
            this.children.front(parentNodeUI)
            dragContext.apply {
                initialX = parentNodeUI.layoutX
                initialY = parentNodeUI.layoutY
                offsetX = event.sceneX
                offsetY = event.sceneY
            }
        }
    }

    private fun onMouseDragged(event: MouseEvent) {
        val node = event.target as? HBox ?: return
        if (node.id == "dragbox") {
            val parentNodeUI = node.parent as? NodeUIElement ?: return
            parentNodeUI.layoutX = dragContext.initialX + event.sceneX - dragContext.offsetX
            parentNodeUI.layoutY = dragContext.initialY + event.sceneY - dragContext.offsetY
        }
    }


    fun addNode(node: NodeUIElement) {
        this.children.add(node)
    }

    fun removeNode(uuid: NodeUUID) {
        this.children.removeIf { (it as? NodeUIElement)?.uuid == uuid }
    }

    private class DragContext {
        var initialX: Double = 0.0
        var initialY: Double = 0.0
        var offsetX: Double = 0.0
        var offsetY: Double = 0.0
    }
}

private fun <E> MutableList<E>.front(node: E) {
    this.remove(node)
    this.add(node)
}
