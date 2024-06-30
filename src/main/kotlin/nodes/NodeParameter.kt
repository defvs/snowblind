package nodes

import kotlin.math.roundToInt

typealias NodeParameter = Pair<ParameterType, Double>

data class NodeParameterData(
    var data: Double = 0.0,
    val isExposed: Boolean = true,
)

fun Map<ParameterType, NodeParameterData>.getValue(key: ParameterType) = this[key]!!.data
fun Map<ParameterType, NodeParameterData>.getReadableValue(key: ParameterType) = key.valueConverter(this.getValue(key))

enum class ParameterType(
    val readableName: String,
    val valueConverter: (Double) -> String,
) {
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

    // Color
    Red("Red", ValueConverter.as8bitColor),
    Green("Green", ValueConverter.as8bitColor),
    Blue("Blue", ValueConverter.as8bitColor),
    OpacityMultiplier("Opacity Multiplier", ValueConverter.asDecimal(2)),
    ;

    private object ValueConverter {
        val asInteger: (Double) -> String = { it.roundToInt().toString() }
        fun asDecimal(digits: Int): (Double) -> String = { String.format("%.${digits}d", it) }
        val asDegrees: (Double) -> String = { it.times(360).roundToInt().toString() }
        val as8bitColor: (Double) -> String = { it.times(256).roundToInt().toString() }
    }
}
