package helpers

import javafx.geometry.Point2D
import javafx.scene.Node

inline fun <reified T : Node> Node.findParent(): T {
    var current: Node? = this.parent
    while (current != null) {
        if (current is T) return current
        current = current.parent
    }
    throw NoSuchElementException("No parent of type ${T::class} found")
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