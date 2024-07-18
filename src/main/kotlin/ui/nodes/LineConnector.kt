package ui.nodes

import javafx.scene.shape.Line
import nodes.NodeConnection

class LineConnector(
    val sourceConnector: NodeUIElementCircle,
    val destinationConnector: NodeUIElementCircle,
    val connection: NodeConnection,
) : Line() {

    init {
        viewOrder = -100.0

        sourceConnector.localToSceneTransformProperty().addListener { _, _, newValue ->
            startX = newValue.tx
            startY = newValue.ty
        }
        destinationConnector.localToSceneTransformProperty().addListener { _, _, newValue ->
            endX = newValue.tx
            endY = newValue.ty
        }
        sourceConnector.localToSceneTransform.let {
            startX = it.tx
            startY = it.ty
        }
        destinationConnector.localToSceneTransform.let {
            endX = it.tx
            endY = it.ty
        }
    }
}