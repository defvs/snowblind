package nodes.implementations.special

import helpers.ConnectorUUID
import helpers.NodeUUID
import helpers.ObservablePosition
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import nodes.INodeBase
import nodes.NodeParameter
import nodes.NodeParameterMap
import nodes.helpers.SimpleValueConverters
import nodes.parameters
import ui.nodes.controls.SliderControl

@Serializable(with = MacroNodeSerializer::class)
class MacroNode(
    override val uuid: NodeUUID = NodeUUID(),

    val macroOutputUUID: ConnectorUUID = ConnectorUUID(),

    macroNumberUUID: ConnectorUUID = ConnectorUUID(),
    macroNumber: Int = 1,

    override val position: ObservablePosition = ObservablePosition(),
) : INodeBase {

    override val name = "Macro Node"
    override val description = """
        Parameter Input from your DAW.
    """.trimIndent()

    override val parameters: NodeParameterMap = parameters {
        internal(
            name = "Macro Number",
            range = 1f..32f,
            valueConverter = SimpleValueConverters.AsInteger,
            control = SliderControl(),
            defaultValue = macroNumber.toFloat(),
            uuid = macroNumberUUID,
        )
        output(
            name = "Macro output",
            uuid = macroOutputUUID,
            compute = { throw Exception("compute run on output of MacroNode.") }
        )
    }

    var macroNumber
        get() = (parameters[0] as NodeParameter.ControllableParameter.InternalParameter).value.toInt()
        set(value) {
            (parameters[0] as NodeParameter.ControllableParameter.InternalParameter).value = value.toFloat()
        }

    init {
        this.macroNumber = macroNumber
    }
}


class MacroNodeSerializer : KSerializer<MacroNode> {
    @Serializable
    private data class Surrogate(
        val uuid: NodeUUID,
        val outputUUID: ConnectorUUID,
        val macroNumberUUID: ConnectorUUID,
        val macroNumber: Int,
        val position: ObservablePosition,
    )

    override val descriptor: SerialDescriptor get() = Surrogate.serializer().descriptor
    override fun serialize(encoder: Encoder, value: MacroNode) = encoder.encodeSerializableValue(
        Surrogate.serializer(),
        Surrogate(value.uuid, value.parameters[1].uuid, value.parameters[0].uuid, value.macroNumber, value.position)
    )

    override fun deserialize(decoder: Decoder) = decoder.decodeSerializableValue(Surrogate.serializer())
        .let { (uuid, outputUUID, macroNumberUUID, macroNumber, position) ->
            MacroNode(
                uuid,
                outputUUID,
                macroNumberUUID,
                macroNumber,
                position
            )
        }
}