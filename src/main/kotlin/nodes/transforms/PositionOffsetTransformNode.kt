package nodes.transforms

import laser.LaserObject
import nodes.NodeBase
import nodes.NodeParameterData
import nodes.ParameterType
import nodes.getValue

class PositionOffsetTransformNode : NodeBase(
    name = "Position Offset",
    description = """
        Offsets and/or Rotates the input.
    """.trimIndent()
) {
    override val params = hashMapOf(
        ParameterType.OffsetX to NodeParameterData(),
        ParameterType.OffsetY to NodeParameterData(),
        ParameterType.Rotation to NodeParameterData(),
        ParameterType.RotationAnchorX to NodeParameterData(),
        ParameterType.RotationAnchorY to NodeParameterData(),
    )

    override fun process(inputs: List<List<LaserObject>>): List<LaserObject> {
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