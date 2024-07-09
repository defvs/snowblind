package nodes.implementations.transforms

import com.github.ajalt.colormath.model.HSL
import laser.LaserObject
import nodes.*

class HSVShiftNode : TransformNode(
    name = "HSV Shift",
    description = """
        Shifts the hue, saturation and lightness of the entire input
    """.trimIndent()
), INodeHasInputParams {
    override val inputParams = NodeParameterMap(
        NodeParameter(ParameterType.HueShift),
        NodeParameter(ParameterType.SaturationShift),
        NodeParameter(ParameterType.LightnessShift),
    )

    override fun processLaser(input: List<LaserObject>): List<LaserObject> {
        return input.onEach { laserObject ->
            laserObject.transformColorHSL {
                HSL(
                    (it.h + inputParams[ParameterType.HueShift]!!.data).mod(1.0),
                    (it.s + inputParams[ParameterType.SaturationShift]!!.data).mod(1.0),
                    (it.l + inputParams[ParameterType.LightnessShift]!!.data).mod(1.0)
                )
            }
        }
    }
}