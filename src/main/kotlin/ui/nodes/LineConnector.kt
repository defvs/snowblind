package ui.nodes

import javafx.beans.value.ObservableValue
import javafx.scene.shape.Line
import javafx.scene.transform.Transform
import nodes.NodeConnection
import nodes.NodeConnectionUUID
import ui.editor.NodeCompositorPane

class LineConnector(
    val connection: NodeConnection,
    compositor: NodeCompositorPane
) : Line() {
    val sourceConnector: NodeUIElementCircle
    val destinationConnector: NodeUIElementCircle

    init {
        fun findConnector(uuid: NodeConnectionUUID) =
            compositor.getNode(uuid.nodeUUID)!!.getConnectorCircle(uuid.connectorUUID)

        sourceConnector = findConnector(connection.source)
        destinationConnector = findConnector(connection.dest)

        viewOrder = -100.0

        val updateStartX: (ObservableValue<out Transform>, Transform, Transform) -> Unit = { _, _, _ ->
            val localPoint = compositor.sceneToLocal(sourceConnector.localToScene(sourceConnector.centerX, sourceConnector.centerY))
            startX = localPoint.x
            startY = localPoint.y
        }

        val updateEndX: (ObservableValue<out Transform>, Transform, Transform) -> Unit = { _, _, _ ->
            val localPoint = compositor.sceneToLocal(destinationConnector.localToScene(destinationConnector.centerX, destinationConnector.centerY))
            endX = localPoint.x
            endY = localPoint.y
        }

        sourceConnector.localToSceneTransformProperty().addListener(updateStartX)
        destinationConnector.localToSceneTransformProperty().addListener(updateEndX)

        val initialStart = compositor.sceneToLocal(sourceConnector.localToScene(sourceConnector.centerX, sourceConnector.centerY))
        startX = initialStart.x
        startY = initialStart.y

        val initialEnd = compositor.sceneToLocal(destinationConnector.localToScene(destinationConnector.centerX, destinationConnector.centerY))
        endX = initialEnd.x
        endY = initialEnd.y
    }
}
