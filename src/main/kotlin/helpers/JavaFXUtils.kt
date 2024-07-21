package helpers

import javafx.geometry.Insets
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

inline fun <reified T : Node> Node.findParent(): Node? {
    var current: Node? = this.parent
    while (current != null) {
        if (current is T) return current
        current = current.parent
    }
    return null
}

fun Node.coordinateToParent(parent: Node, position: Point2D): Point2D {
    var currentPosition = position
    var current: Node? = this
    while (current != null) {
        currentPosition = current.localToParent(currentPosition)
        current = current.parent
        if (current == parent) return currentPosition
    }
    throw NoSuchElementException("Parent was not found in the hierarchy")
}

fun HBox(apply: HBox.() -> Unit) = HBox().apply(apply)
fun VBox(apply: VBox.() -> Unit) = VBox().apply(apply)
fun StackPane(apply: StackPane.() -> Unit) = StackPane().apply(apply)

fun Insets(topbottom: Double, leftright: Double): Insets = Insets(topbottom, leftright, topbottom, leftright)