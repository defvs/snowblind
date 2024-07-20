package nodes.implementations.generators

import com.github.ajalt.colormath.model.RGB
import helpers.ConnectorUUID
import helpers.NodeUUID
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import laser.LaserObject
import laser.Point
import nodes.GeneratorNode
import nodes.INodeHasInputParams
import nodes.controls.EmptyControl
import nodes.helpers.SimpleValueConverters
import nodes.helpers.SimpleValueRanges
import nodes.parameters

@Serializable(with = PointGeneratorNodeSerializer::class)
class PointGeneratorNode(
    override val uuid: NodeUUID = NodeUUID(),
    override val laserOutputUUID: ConnectorUUID = ConnectorUUID(),
    inputParamValues: List<Float>? = null,
) : GeneratorNode(
    name = "Point Generator",
    description = """
        Generates a single point
    """.trimIndent(),
), INodeHasInputParams {
    override val inputParams = parameters {
        parameter(
            name = "X",
            range = SimpleValueRanges.position,
            valueConverter = SimpleValueConverters.asInteger,
            control = EmptyControl()
        )
        parameter(
            name = "Y",
            range = SimpleValueRanges.position,
            valueConverter = SimpleValueConverters.asInteger,
            control = EmptyControl()
        )
        parameter(
            name = "Red",
            range = SimpleValueRanges.color,
            valueConverter = SimpleValueConverters.asInteger,
            control = EmptyControl()
        )
        parameter(
            name = "Green",
            range = SimpleValueRanges.color,
            valueConverter = SimpleValueConverters.asInteger,
            control = EmptyControl()
        )
        parameter(
            name = "Blue",
            range = SimpleValueRanges.color,
            valueConverter = SimpleValueConverters.asInteger,
            control = EmptyControl()
        )
    }

    init {
        if (inputParamValues != null)
            inputParams.parameters.forEachIndexed { i, param -> param.data = inputParamValues[i] }
    }

    override val laserOutput: List<LaserObject>
        get() = listOf(
            LaserObject(
                Point(
                    inputParams[0].data,
                    inputParams[1].data,
                ), RGB(
                    inputParams[2].data,
                    inputParams[3].data,
                    inputParams[4].data,
                )
            )
        )
}

class PointGeneratorNodeSerializer : KSerializer<PointGeneratorNode> {
    @Serializable
    private data class Surrogate(
        val uuid: NodeUUID,
        val laserOutputUUID: ConnectorUUID,
        val parameterValues: List<Float>,
    )

    override val descriptor: SerialDescriptor get() = Surrogate.serializer().descriptor
    override fun serialize(encoder: Encoder, value: PointGeneratorNode) =
        encoder.encodeSerializableValue(Surrogate.serializer(), Surrogate(
            value.uuid,
            value.laserOutputUUID,
            value.inputParams.parameters.map { it.data }
        ))

    override fun deserialize(decoder: Decoder) =
        decoder.decodeSerializableValue(Surrogate.serializer()).let { (uuid, laserOutputUUID, parameterValues) ->
            PointGeneratorNode(uuid, laserOutputUUID, parameterValues)
        }
}