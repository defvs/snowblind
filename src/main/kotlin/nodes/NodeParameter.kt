package nodes

import helpers.ConnectorUUID
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.math.roundToInt

@Serializable
data class NodeParameter(
    val type: ParameterType,
    var data: Double = 0.0,
    @Required val isExposed: Boolean = true,
    @Required val uuid: ConnectorUUID = ConnectorUUID(),
)

@Serializable(with = NodeParameterMapSerializer::class)
class NodeParameterMap(vararg params: NodeParameter) : Iterable<NodeParameter> {
    private val mapByUUID = params.associateBy { it.uuid }
    private val mapByType = params.associateBy { it.type }

    val parameters: Collection<NodeParameter>
        get() = mapByUUID.values

    operator fun get(uuid: ConnectorUUID) = mapByUUID[uuid]
    operator fun get(type: ParameterType) = mapByType[type]

    fun getValue(uuid: ConnectorUUID) = mapByUUID[uuid]!!.data
    fun getValue(type: ParameterType) = mapByType[type]!!.data

    operator fun set(uuid: ConnectorUUID, data: Double) {
        mapByUUID[uuid]?.data = data
    }

    operator fun set(type: ParameterType, data: Double) {
        mapByType[type]?.data = data
    }

    override fun iterator(): Iterator<NodeParameter> {
        return mapByUUID.values.iterator()
    }
}

class NodeParameterMapSerializer : KSerializer<NodeParameterMap> {
    @OptIn(ExperimentalSerializationApi::class)
    private val delegateSerializer = ArraySerializer(NodeParameter.serializer())
    override val descriptor = delegateSerializer.descriptor

    override fun deserialize(decoder: Decoder): NodeParameterMap {
        return NodeParameterMap(*decoder.decodeSerializableValue(delegateSerializer))
    }

    override fun serialize(encoder: Encoder, value: NodeParameterMap) {
        encoder.encodeSerializableValue(delegateSerializer, value.parameters.toTypedArray())
    }

}

@Serializable
enum class ParameterType(
    val readableName: String,
    val valueConverter: (Double) -> String,
) {
    // Macro / Others
    Generic("Generic", ValueConverter.asInteger),

    // Base positions
    BasePosX("X Position", ValueConverter.asInteger),
    BasePosY("Y Position", ValueConverter.asInteger),

    // Offset positions
    OffsetX("X Offset", ValueConverter.asInteger),
    OffsetY("Y Offset", ValueConverter.asInteger),

    // Rotation
    Rotation("Rotation", ValueConverter.asDegrees),
    RotationAnchorX("Rotation X Anchor", ValueConverter.asInteger),
    RotationAnchorY("Rotation Y Anchor", ValueConverter.asInteger),

    // Color (RGB & HSL)
    Red("Red", ValueConverter.as8bitColor),
    Green("Green", ValueConverter.as8bitColor),
    Blue("Blue", ValueConverter.as8bitColor),
    HueShift("Hue Shift", ValueConverter.as8bitColor),
    SaturationShift("Saturation Shift", ValueConverter.as8bitColor),
    LightnessShift("Lightness Shift", ValueConverter.as8bitColor),
    ;

    private object ValueConverter {
        val asInteger: (Double) -> String = { it.roundToInt().toString() }
        fun asDecimal(digits: Int): (Double) -> String = { String.format("%.${digits}d", it) }
        val asDegrees: (Double) -> String = { it.times(360).roundToInt().toString() }
        val as8bitColor: (Double) -> String = { it.times(256).roundToInt().toString() }
    }
}
