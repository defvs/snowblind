package nodes.implementations.transforms

import com.github.ajalt.colormath.model.HSL
import helpers.ConnectorUUID
import helpers.NodeUUID
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import laser.LaserObject
import nodes.INodeHasInputParams
import nodes.TransformNode
import nodes.controls.EmptyControl
import nodes.helpers.SimpleValueConverters
import nodes.helpers.SimpleValueRanges
import nodes.parameters

@Serializable(with = HSVShiftNodeSerializer::class)
class HSVShiftNode(
    override val uuid: NodeUUID = NodeUUID(),
    override val laserInputUUID: ConnectorUUID = ConnectorUUID(),
    override val laserOutputUUID: ConnectorUUID = ConnectorUUID(),
    inputParamValues: List<Float>? = null,
) : TransformNode(
    name = "HSV Shift",
    description = """
        Shifts the hue, saturation and lightness of the entire input
    """.trimIndent(),
), INodeHasInputParams {

    override val inputParams = parameters {
        parameter(
            name = "Hue Shift",
            range = SimpleValueRanges.color,
            valueConverter = SimpleValueConverters.as8bitColor,
            control = EmptyControl()
        )
        parameter(
            name = "Saturation Shift",
            range = SimpleValueRanges.color,
            valueConverter = SimpleValueConverters.as8bitColor,
            control = EmptyControl()
        )
        parameter(
            name = "Lightness Shift",
            range = SimpleValueRanges.color,
            valueConverter = SimpleValueConverters.as8bitColor,
            control = EmptyControl()
        )
    }

    init {
        if (inputParamValues != null)
            inputParams.parameters.forEachIndexed { i, param -> param.data = inputParamValues[i] }
    }

    override fun processLaser(input: List<LaserObject>) = input.onEach { laserObject ->
        laserObject.transformColorHSL {
            HSL(
                (it.h + inputParams[0].data).mod(1.0),
                (it.s + inputParams[1].data).mod(1.0),
                (it.l + inputParams[2].data).mod(1.0)
            )
        }
    }
}

class HSVShiftNodeSerializer : KSerializer<HSVShiftNode> {
    @Serializable
    private data class Surrogate(
        val uuid: NodeUUID,
        val laserInputUUID: ConnectorUUID,
        val laserOutputUUID: ConnectorUUID,
        val parameterValues: List<Float>,
    )

    override val descriptor: SerialDescriptor get() = Surrogate.serializer().descriptor
    override fun serialize(encoder: Encoder, value: HSVShiftNode) =
        encoder.encodeSerializableValue(Surrogate.serializer(), Surrogate(
            value.uuid,
            value.laserInputUUID,
            value.laserOutputUUID,
            value.inputParams.parameters.map { it.data }
        ))

    override fun deserialize(decoder: Decoder) = decoder.decodeSerializableValue(Surrogate.serializer())
        .let { (uuid, laserInputUUID, laserOutputUUID, parameterValues) ->
            HSVShiftNode(uuid, laserInputUUID, laserOutputUUID, parameterValues)
        }
}