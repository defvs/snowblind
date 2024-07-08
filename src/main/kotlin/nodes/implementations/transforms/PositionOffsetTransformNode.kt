package nodes.implementations.transforms

import laser.LaserObject
import nodes.*

class PositionOffsetTransformNode : TransformNode(
    name = "Position Offset",
    description = """
        Offsets and/or Rotates the input.
    """.trimIndent()
), INodeHasInputParams {
    override val inputParams = NodeParameterMap(
        NodeParameter(ParameterType.OffsetX),
        NodeParameter(ParameterType.OffsetY),
        NodeParameter(ParameterType.Rotation),
        NodeParameter(ParameterType.RotationAnchorX),
        NodeParameter(ParameterType.RotationAnchorY),
    )

    override fun processLaser(input: List<LaserObject>): List<LaserObject> {
        return input.onEach { laserObject ->
            laserObject.points.onEach {
                it.offset(
                    inputParams.getValue(ParameterType.OffsetX),
                    inputParams.getValue(ParameterType.OffsetY),
                )
                it.rotate(
                    inputParams.getValue(ParameterType.Rotation),
                    inputParams.getValue(ParameterType.RotationAnchorX),
                    inputParams.getValue(ParameterType.RotationAnchorY),
                )
            }
        }
    }
}