package laser

class LaserObject(val laserPoints: List<LaserPoint>, val closedPolygon: Boolean) {
    val points: List<Point>
        get() = laserPoints.map { it.point }

    constructor(point: Point, color: Color) : this(listOf(LaserPoint(point, color)), false)

    class LaserPoint(val point: Point, val colors: ArrayList<Color>, var gradientType: GradientType) {
        constructor(point: Point, color: Color) : this(point, arrayListOf(color), GradientType.None)
    }

    fun applyPositionTransform(transform: Point.() -> Unit) = points.forEach(transform)
    fun applyColorTransform(transform: Color.() -> Unit) = laserPoints.flatMap { it.colors }.forEach(transform)
}

enum class GradientType {
    Smooth, Steps, None,
}
