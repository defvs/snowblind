package nodes.dataflow

typealias Parameter = Pair<ParameterType, Double>

data class NodeParameterData(
    var data: Double = 0.0,
    val isExposed: Boolean = true,
)

fun Map<ParameterType, NodeParameterData>.getValue(key: ParameterType) = this[key]!!.data

enum class ParameterType(readableName: String) { // TODO: add human-printable value lambda to each
    // Base positions
    BasePosX("X Position"), BasePosY("Y Position"),

    // Offset positions
    OffsetX("X Offset"), OffsetY("Y Offset"),

    // Rotation
    Rotation("Rotation"),
    RotationAnchorX("Rotation X Anchor"),
    RotationAnchorY("Rotation Y Anchor"),

    // Color
    Red("Red"), Green("Green"), Blue("Blue"), OpacityMultiplier("Opacity Multiplier"),
}
