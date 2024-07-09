package laser

import com.github.ajalt.colormath.model.HSL
import com.github.ajalt.colormath.model.RGB

class LaserObject(val laserPoints: List<LaserPoint>, val closedPolygon: Boolean) {
    val points: List<Point>
        get() = laserPoints.map { it.point }

    constructor(point: Point, color: RGB) : this(listOf(LaserPoint(point, color)), false)

    class LaserPoint(val point: Point, val colors: ArrayList<RGB>, var gradientType: GradientType) {
        constructor(point: Point, color: RGB) : this(point, arrayListOf(color), GradientType.None)
    }

    fun applyPositionTransform(transform: Point.() -> Unit) = points.forEach(transform)
    fun transformColorRGB(transform: (RGB) -> RGB) =
        laserPoints.forEach { laserPoint -> laserPoint.colors.replaceAll { transform(it) } }
    fun transformColorHSL(transform: (HSL) -> HSL) =
        laserPoints.forEach { laserPoint -> laserPoint.colors.replaceAll { transform(it.toHSL()).toSRGB() } }
}

enum class GradientType {
    Smooth, Steps, None,
}
