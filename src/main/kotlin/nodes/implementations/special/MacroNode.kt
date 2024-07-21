package nodes.implementations.special

import helpers.NodeUUID
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import nodes.INodeBase
import nodes.INodeHasInternalParams
import nodes.INodeHasOutputParams
import ui.nodes.controls.EmptyControl
import nodes.helpers.SimpleValueConverters
import nodes.helpers.SimpleValueRanges
import nodes.parameters

@Serializable(with = MacroNodeSerializer::class)
class MacroNode(
    override val uuid: NodeUUID = NodeUUID(),
    macroNumber: Int = 1,
) : INodeBase, INodeHasOutputParams, INodeHasInternalParams {
    @Transient override val name = "Macro Node"
    @Transient override val description = """
        Parameter Input from your DAW.
    """.trimIndent()

    override val outputParams = parameters {
        parameter(
            name = "Macro output",
            range = SimpleValueRanges.infinite,
            valueConverter = SimpleValueConverters.asDecimal(2),
            control = EmptyControl()
        )
    }

    override val internalParams = parameters {
        parameter(
            name = "Macro Number",
            range = 1f..32f,
            valueConverter = SimpleValueConverters.asInteger,
            control = EmptyControl(),
            defaultValue = 1f
        )
    }

    var macroOutput
        get() = outputParams[0]
        set(value) {
            outputParams[0] = value
        }
    var macroNumber
        get() = internalParams[0].toInt()
        set(value) {
            internalParams[0] = value.toFloat()
        }

    init {
        this.macroNumber = macroNumber
    }
}


class MacroNodeSerializer : KSerializer<MacroNode> {
    @Serializable
    private data class Surrogate(
        val uuid: NodeUUID,
        val macroNumber: Int,
    )

    override val descriptor: SerialDescriptor get() = Surrogate.serializer().descriptor
    override fun serialize(encoder: Encoder, value: MacroNode) = encoder.encodeSerializableValue(
        Surrogate.serializer(), Surrogate(value.uuid, value.macroNumber)
    )

    override fun deserialize(decoder: Decoder) = decoder.decodeSerializableValue(Surrogate.serializer())
        .let { (uuid, macroNumber) -> MacroNode(uuid, macroNumber) }
}