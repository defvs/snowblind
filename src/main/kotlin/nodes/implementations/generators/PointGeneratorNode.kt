package nodes.implementations.generators

import com.github.ajalt.colormath.model.RGB
import laser.LaserObject
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
    )

    override val laserOutput: List<LaserObject>
        get() = listOf(
            LaserObject(
                Point(
                    inputParams.getValue(ParameterType.BasePosX),
                    inputParams.getValue(ParameterType.BasePosY),
                ), RGB(
                    inputParams.getValue(ParameterType.Red),
                    inputParams.getValue(ParameterType.Green),
                    inputParams.getValue(ParameterType.Blue),
                )
            )
        )
}