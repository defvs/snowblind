package ui.nodes

import javafx.scene.shape.Line
import nodes.NodeConnection
import javafx.beans.value.ObservableValue
import ui.editor.NodeCompositorPane

class LineConnector(
    val sourceConnector: NodeUIElementCircle,
    val destinationConnector: NodeUIElementCircle,
    val connection: NodeConnection,
    private val pane: NodeCompositorPane
) : Line() {

    init {
        viewOrder = -100.0

        val updateStartX: (ObservableValue<out javafx.scene.transform.Transform>, javafx.scene.transform.Transform, javafx.scene.transform.Transform) -> Unit = { _, _, _ ->
            val localPoint = pane.sceneToLocal(sourceConnector.localToScene(sourceConnector.centerX, sourceConnector.centerY))
            startX = localPoint.x
            startY = localPoint.y
        }

        val updateEndX: (ObservableValue<out javafx.scene.transform.Transform>, javafx.scene.transform.Transform, javafx.scene.transform.Transform) -> Unit = { _, _, _ ->
            val localPoint = pane.sceneToLocal(destinationConnector.localToScene(destinationConnector.centerX, destinationConnector.centerY))
            endX = localPoint.x
            endY = localPoint.y
        }

        sourceConnector.localToSceneTransformProperty().addListener(updateStartX)
        destinationConnector.localToSceneTransformProperty().addListener(updateEndX)

        val initialStart = pane.sceneToLocal(sourceConnector.localToScene(sourceConnector.centerX, sourceConnector.centerY))
        startX = initialStart.x
        startY = initialStart.y

        val initialEnd = pane.sceneToLocal(destinationConnector.localToScene(destinationConnector.centerX, destinationConnector.centerY))
        endX = initialEnd.x
        endY = initialEnd.y
    }
}
