package ui.nodes

import helpers.ConnectorUUID
import helpers.NodeUUID
import javafx.scene.paint.Paint
import javafx.scene.shape.Circle

class NodeUIElementCircle(
    radius: Double,
    fill: Paint,
    val connectorUUID: ConnectorUUID,
    val parentNodeUUID: NodeUUID,
    val connectorType: ConnectorType,
) : Circle(radius, fill)

enum class ConnectorType(val isInput: Boolean) {
    LaserInput(true),
    LaserOutput(false),
    ParameterInput(true),
    ParameterOutput(false),
    ;

    val opposite: ConnectorType
        get() = when (this) {
            LaserInput -> LaserOutput
            LaserOutput -> LaserInput
            ParameterInput -> ParameterOutput
            ParameterOutput -> ParameterInput
        }
}