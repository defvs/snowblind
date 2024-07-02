package nodes.implementations.generators

import laser.Color
import laser.LaserObject
import laser.LaserPoint
import laser.Point
import nodes.*

class PointGeneratorNode : GeneratorNode(
    name = "Point Generator",
    description = """
        Generates a single point
    """.trimIndent()
), INodeHasInputParams {
    override val inputParams = NodeParameterMap(
        NodeParameter(ParameterType.BasePosX),
        NodeParameter(ParameterType.BasePosY),
        NodeParameter(ParameterType.Red),
        NodeParameter(ParameterType.Green),
        NodeParameter(ParameterType.Blue),
        NodeParameter(ParameterType.OpacityMultiplier),
    )

    override val laserOutput: List<LaserObject>
        get() = listOf(
            LaserPoint(
                Point(
                    inputParams.getValue(ParameterType.BasePosX),
                    inputParams.getValue(ParameterType.BasePosY),
                ), Color(
                    inputParams.getValue(ParameterType.Red),
                    inputParams.getValue(ParameterType.Green),
                    inputParams.getValue(ParameterType.Blue),
                    inputParams.getValue(ParameterType.OpacityMultiplier),
                )
            )
        )
}