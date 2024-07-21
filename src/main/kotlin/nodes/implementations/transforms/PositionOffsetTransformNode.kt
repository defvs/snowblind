package nodes.implementations.transforms

import helpers.ConnectorUUID
import helpers.NodeUUID
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import laser.LaserObject
import nodes.INodeHasInputParams
import nodes.TransformNode
import ui.nodes.controls.EmptyControl
import nodes.helpers.SimpleValueConverters
import nodes.helpers.SimpleValueRanges
import nodes.parameters

@Serializable(with = PositionOffsetTransformNodeSerializer::class)
class PositionOffsetTransformNode(
    override val uuid: NodeUUID = NodeUUID(),
    override val laserInputUUID: ConnectorUUID = ConnectorUUID(),
    override val laserOutputUUID: ConnectorUUID = ConnectorUUID(),
    inputParamValues: List<Float>? = null,
) : TransformNode(
    name = "Position Offset",
    description = """
        Offsets and/or Rotates the input.
    """.trimIndent()
), INodeHasInputParams {

    @Required override val inputParams = parameters {
        parameter("X Offset", SimpleValueRanges.position, SimpleValueConverters.asInteger, EmptyControl())
        parameter("Y Offset", SimpleValueRanges.position, SimpleValueConverters.asInteger, EmptyControl())
        parameter("Rotation", SimpleValueRanges.rotation, SimpleValueConverters.asDegrees, EmptyControl())
        parameter("Rotation X Anchor", SimpleValueRanges.position, SimpleValueConverters.asInteger, EmptyControl())
        parameter("Rotation Y Anchor", SimpleValueRanges.position, SimpleValueConverters.asInteger, EmptyControl())
    }

    init {
        if (inputParamValues != null)
            inputParams.parameters.forEachIndexed { i, param -> param.data = inputParamValues[i] }
    }

    override fun processLaser(input: List<LaserObject>) = input.onEach { laserObject ->
        laserObject.applyPositionTransform {
            offset(
                inputParams[0],
                inputParams[1],
            )
            rotate(
                inputParams[2],
                inputParams[3],
                inputParams[4],
            )
        }
    }
}

class PositionOffsetTransformNodeSerializer : KSerializer<PositionOffsetTransformNode> {
    @Serializable
    private data class Surrogate(
        val uuid: NodeUUID,
        val laserInputUUID: ConnectorUUID,
        val laserOutputUUID: ConnectorUUID,
        val parameterValues: List<Float>,
    )

    override val descriptor: SerialDescriptor get() = Surrogate.serializer().descriptor
    override fun serialize(encoder: Encoder, value: PositionOffsetTransformNode) =
        encoder.encodeSerializableValue(Surrogate.serializer(), Surrogate(
            value.uuid,
            value.laserInputUUID,
            value.laserOutputUUID,
            value.inputParams.parameters.map { it.data }
        ))

    override fun deserialize(decoder: Decoder) = decoder.decodeSerializableValue(Surrogate.serializer())
        .let { (uuid, laserInputUUID, laserOutputUUID, parameterValues) ->
            PositionOffsetTransformNode(uuid, laserInputUUID, laserOutputUUID, parameterValues)
        }
}