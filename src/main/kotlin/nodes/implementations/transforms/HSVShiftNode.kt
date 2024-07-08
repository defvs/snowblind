package nodes.implementations.transforms

import laser.LaserObject
import nodes.*

class HSVShiftNode : TransformNode(
    name = "HSV Shift",
    description = """
        Shifts the hue and saturation of the entire input
    """.trimIndent()
), INodeHasInputParams {
    override val inputParams = NodeParameterMap(
        NodeParameter(ParameterType.HueShift),
        NodeParameter(ParameterType.SaturationShift),
    )

    override fun processLaser(input: List<LaserObject>): List<LaserObject> {
        return input.onEach { laserObject ->
            laserObject.laserPoints.onEach {
                it.colors.forEach { color ->
                    color.hue = (color.hue + inputParams[ParameterType.HueShift]!!.data).mod(1.0)
                    color.saturation = (color.saturation + inputParams[ParameterType.SaturationShift]!!.data).mod(1.0)
                }
            }
        }
    }
}