package nodes.generators

import io.github.oshai.kotlinlogging.KotlinLogging
import nodes.dataflow.*
import java.util.*

private val logger = KotlinLogging.logger {}

class PointGeneratorNode(uuid: UUID, name: String) : NodeBase(uuid, name) {
    override val params = hashMapOf(
        ParameterType.BasePosX to NodeParameterData(),
        ParameterType.BasePosY to NodeParameterData(),
        ParameterType.Red to NodeParameterData(),
        ParameterType.Green to NodeParameterData(),
        ParameterType.Blue to NodeParameterData(),
        ParameterType.OpacityMultiplier to NodeParameterData(),
    )

    override fun process(inputs: List<DataFlow>): DataFlow {
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