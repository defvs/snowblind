package nodes.implementations.transforms

import com.github.ajalt.colormath.model.HSL
import helpers.ConnectorUUID
import helpers.NodeUUID
import helpers.serialization.nodes.TransformNodeSerializer
import kotlinx.serialization.Serializable
import laser.LaserObject
import nodes.TransformNode
import nodes.controls.EmptyControl
import nodes.helpers.SimpleValueConverters
import nodes.helpers.SimpleValueRanges
import nodes.mapToInput
import nodes.parameters

@Serializable(with = HSVShiftNodeSerializer::class)
class HSVShiftNode(
    override val uuid: NodeUUID = NodeUUID(),
    override val laserInputUUID: ConnectorUUID = ConnectorUUID(),
    override val laserOutputUUID: ConnectorUUID = ConnectorUUID(),
    existingUUIDs: List<ConnectorUUID>,
    existingValues: Map<ConnectorUUID, Float>,
) : TransformNode {

    override val name = "HSV Shift"
    override val description = """
        Shifts the hue, saturation and lightness of the entire input
    """.trimIndent()

    override val parameters = parameters {
        internalControllable(
            name = "Hue Shift",
            range = SimpleValueRanges.color,
            valueConverter = SimpleValueConverters.as8bitColor,
            control = EmptyControl()
        )
        internalControllable(
            name = "Saturation Shift",
            range = SimpleValueRanges.color,
            valueConverter = SimpleValueConverters.as8bitColor,
            control = EmptyControl()
        )
        internalControllable(
            name = "Lightness Shift",
            range = SimpleValueRanges.color,
            valueConverter = SimpleValueConverters.as8bitColor,
            control = EmptyControl()
        )

        withExistingUUIDs(existingUUIDs)
        withExistingValues(existingValues)
    }

    override fun transformLaser(
        inputLaser: List<LaserObject>,
        inputParameters: Map<ConnectorUUID, Float>,
    ): List<LaserObject> {
        val data = parameters.mapToInput(inputParameters)
        return inputLaser.onEach { laserObject ->
            laserObject.transformColorHSL {
                HSL(
                    (it.h + data["Hue Shift"]!!).mod(1.0),
                    (it.s + data["Saturation Shift"]!!).mod(1.0),
                    (it.l + data["Lightness Shift"]!!).mod(1.0)
                )
            }
        }
    }
}

class HSVShiftNodeSerializer : TransformNodeSerializer<HSVShiftNode>({
    HSVShiftNode(
        it.uuid,
        it.laserInputUUID,
        it.laserOutputUUID,
        it.parametersUUIDs,
        it.internalParametersValues
    )
})