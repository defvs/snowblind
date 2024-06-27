package nodes.transforms

import nodes.dataflow.*
import java.util.*

class PositionOffsetTransformNode(uuid: UUID, name: String) : NodeBase(uuid, name) {
    override val params = hashMapOf(
        ParameterType.OffsetX to NodeParameterData(),
        ParameterType.OffsetY to NodeParameterData(),
        ParameterType.Rotation to NodeParameterData(),
        ParameterType.RotationAnchorX to NodeParameterData(),
        ParameterType.RotationAnchorY to NodeParameterData(),
    )

    override fun process(inputs: List<DataFlow>): DataFlow {
        if (inputs.isEmpty()) return listOf()
        return inputs.flatten().onEach { laserObject ->
            laserObject.points.onEach {
                it.offset(
                    params.getValue(ParameterType.OffsetX),
                    params.getValue(ParameterType.OffsetY),
                )
                it.rotate(
                    params.getValue(ParameterType.Rotation),
                    params.getValue(ParameterType.RotationAnchorX),
                    params.getValue(ParameterType.RotationAnchorY),
                )
            }
        }
    }
}