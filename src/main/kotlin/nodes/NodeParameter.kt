package nodes

import helpers.ConnectorUUID
import helpers.serialization.NodeParameterMapSerializer
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

/**
 * Data class representing a node parameter with a UUID and associated data.
 */
@Serializable
data class NodeParameter(
    val type: ParameterType,
    var data: Float = 0.0f,
    @Required val isExposed: Boolean = true,
    @Required val uuid: ConnectorUUID = ConnectorUUID(),
)

/**
 * Class representing a map of node parameters.
 */
@Serializable(with = NodeParameterMapSerializer::class)
class NodeParameterMap(vararg parameters: NodeParameter) : Iterable<NodeParameter> {
    private val mapByUUID = parameters.associateBy { it.uuid }
    private val mapByType = parameters.associateBy { it.type }

    val parameters: Collection<NodeParameter>
        get() = mapByUUID.values

    operator fun get(uuid: ConnectorUUID) = mapByUUID[uuid]
    operator fun get(type: ParameterType) = mapByType[type]

    fun getValue(uuid: ConnectorUUID) = mapByUUID[uuid]!!.data
    fun getValue(type: ParameterType) = mapByType[type]!!.data

    operator fun set(uuid: ConnectorUUID, data: Float) {
        mapByUUID[uuid]?.data = data
    }

    operator fun set(type: ParameterType, data: Float) {
        mapByType[type]?.data = data
    }

    override fun iterator(): Iterator<NodeParameter> {
        return mapByUUID.values.iterator()
    }
}

/**
 * Enum class representing types of parameters with associated readable names and value converters.
 *
 * @property readableName The human-readable name of the parameter type.
 * @property valueConverter Function to convert the parameter value to a readable string.
 */
@Serializable
enum class ParameterType(
    val readableName: String,
    val valueConverter: (Float) -> String,
) {
    // Macro / Others
    Generic("Generic", ValueConverter.asInteger),
    Index("Index", ValueConverter.asInteger),

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
        val asInteger: (Float) -> String = { it.roundToInt().toString() }
        fun asDecimal(digits: Int): (Float) -> String = { String.format("%.${digits}d", it) }
        val asDegrees: (Float) -> String = { it.times(360).roundToInt().toString() }
        val as8bitColor: (Float) -> String = { it.times(256).roundToInt().toString() }
    }
}
