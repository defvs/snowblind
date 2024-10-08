package nodes.implementations.transforms

import helpers.ConnectorUUID
import helpers.NodeUUID
import helpers.ObservablePosition
import helpers.serialization.nodes.TransformNodeSerializer
import javafx.beans.property.FloatProperty
import kotlinx.serialization.Serializable
import laser.LaserObject
import nodes.TransformNode
import nodes.helpers.SimpleValueConverters
import nodes.helpers.SimpleValueRanges
import nodes.mapToInput
import nodes.parameters
import ui.nodes.controls.SliderControl

@Serializable(with = PositionOffsetTransformNodeSerializer::class)
class PositionOffsetTransformNode(
    override val uuid: NodeUUID = NodeUUID(),
    override val laserInputUUID: ConnectorUUID = ConnectorUUID(),
    override val laserOutputUUID: ConnectorUUID = ConnectorUUID(),
    existingUUIDs: List<ConnectorUUID>? = null,
    existingValues: Map<ConnectorUUID, Float>? = null,
    override val position: Pair<FloatProperty, FloatProperty> = ObservablePosition(),
) : TransformNode {

    override val name = "Position Offset"
    override val description = """
        Offsets and/or Rotates the input.
    """.trimIndent()

    override val parameters = parameters {
        internalControllable(
            name = "X Offset",
            range = SimpleValueRanges.position,
            valueConverter = SimpleValueConverters.AsInteger,
            control = SliderControl()
        )
        internalControllable(
            name = "Y Offset",
            range = SimpleValueRanges.position,
            valueConverter = SimpleValueConverters.AsInteger,
            control = SliderControl()
        )
        internalControllable(
            name = "Rotation",
            range = SimpleValueRanges.rotation,
            valueConverter = SimpleValueConverters.AsDegrees,
            control = SliderControl()
        )
        internalControllable(
            name = "Rotation X Anchor",
            range = SimpleValueRanges.position,
            valueConverter = SimpleValueConverters.AsInteger,
            control = SliderControl()
        )
        internalControllable(
            name = "Rotation Y Anchor",
            range = SimpleValueRanges.position,
            valueConverter = SimpleValueConverters.AsInteger,
            control = SliderControl()
        )

        existingUUIDs?.let { withExistingUUIDs(it) }
        existingValues?.let { withExistingValues(it) }
    }

    override fun transformLaser(
        inputLaser: List<LaserObject>,
        inputParameters: Map<ConnectorUUID, Float>,
    ): List<LaserObject> {
        val data = parameters.mapToInput(inputParameters)
        return inputLaser.onEach { laserObject ->
            laserObject.applyPositionTransform {
                offset(
                    data["X Offset"]!!,
                    data["Y Offset"]!!
                )
                rotate(
                    data["Rotation"]!!,
                    data["Rotation X Anchor"]!!,
                    data["Rotation Y Anchor"]!!
                )
            }
        }
    }
}

class PositionOffsetTransformNodeSerializer : TransformNodeSerializer<PositionOffsetTransformNode>({
    PositionOffsetTransformNode(
        it.uuid,
        it.laserInputUUID,
        it.laserOutputUUID,
        it.parametersUUIDs,
        it.internalParametersValues,
        it.position
    )
}, PositionOffsetTransformNode::class)
