package nodes.generators

import io.github.oshai.kotlinlogging.KotlinLogging
import laser.Color
import laser.LaserObject
import laser.LaserPoint
import laser.Point
import nodes.GeneratorNode
import nodes.NodeParameterData
import nodes.ParameterType
import nodes.getValue

private val logger = KotlinLogging.logger {}

class PointGeneratorNode : GeneratorNode(
    name = "Point Generator",
    description = """
        Generates a single point
    """.trimIndent()
) {
    override val params = hashMapOf(
        ParameterType.BasePosX to NodeParameterData(),
        ParameterType.BasePosY to NodeParameterData(),
        ParameterType.Red to NodeParameterData(),
        ParameterType.Green to NodeParameterData(),
        ParameterType.Blue to NodeParameterData(),
        ParameterType.OpacityMultiplier to NodeParameterData(),
    )

    override fun process(inputs: List<List<LaserObject>>): List<LaserObject> {
        if (inputs.isNotEmpty()) logger.warn { "Input isn't empty in a Generator Node." }
        return listOf(
            LaserPoint(
                Point(
                    params.getValue(ParameterType.BasePosX),
                    params.getValue(ParameterType.BasePosY),
                ), Color(
                    params.getValue(ParameterType.Red),
                    params.getValue(ParameterType.Green),
                    params.getValue(ParameterType.Blue),
                    params.getValue(ParameterType.OpacityMultiplier),
                )
            )
        )
    }
}