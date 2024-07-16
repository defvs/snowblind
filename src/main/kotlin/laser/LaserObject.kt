package laser

import com.github.ajalt.colormath.model.HSL
import com.github.ajalt.colormath.model.RGB

/**
 * Represents a laser object, which may consist of multiple laser points.
 *
 * @property laserPoints A list of LaserPoint objects.
 * @property closedPolygon Indicates if the points form a closed polygon.
 */
class LaserObject(val laserPoints: List<LaserPoint>, val closedPolygon: Boolean) {
    /**
     * Retrieves the points from the laser points.
     */
    val points: List<Point>
        get() = laserPoints.map { it.point }

    /**
     * Constructs a LaserObject with a single point and color.
     *
     * @param point The point.
     * @param color The color of the laser point.
     */
    constructor(point: Point, color: RGB) : this(listOf(LaserPoint(point, color)), false)

    /**
     * Represents a single laser point with color information.
     *
     * @property point The point.
     * @property colors The colors associated with the point.
     * @property gradientType The gradient type.
     */
    class LaserPoint(val point: Point, val colors: ArrayList<RGB>, var gradientType: GradientType) {
        constructor(point: Point, color: RGB) : this(point, arrayListOf(color), GradientType.None)
    }

    /**
     * Applies a position transformation to all points.
     *
     * @param transform The transformation function to apply.
     */
    fun applyPositionTransform(transform: Point.() -> Unit) = points.forEach(transform)

    /**
     * Transforms the RGB color of all laser points.
     *
     * @param transform The transformation function to apply.
     */
    fun transformColorRGB(transform: (RGB) -> RGB) =
        laserPoints.forEach { laserPoint -> laserPoint.colors.replaceAll { transform(it) } }

    /**
     * Transforms the HSL color of all laser points.
     *
     * @param transform The transformation function to apply.
     */
    fun transformColorHSL(transform: (HSL) -> HSL) =
        laserPoints.forEach { laserPoint -> laserPoint.colors.replaceAll { transform(it.toHSL()).toSRGB() } }

    /**
     * Scales the polygon by a given factor with respect to an anchor point.
     *
     * @param factor The scaling factor.
     * @param anchorX The x-coordinate of the anchor point.
     * @param anchorY The y-coordinate of the anchor point.
     */
    fun scale(factor: Float, anchorX: Float, anchorY: Float) {
        applyPositionTransform {
            this.x = anchorX + (this.x - anchorX) * factor
            this.y = anchorY + (this.y - anchorY) * factor
        }
    }

    /**
     * Rotates the polygon by a given angle around an anchor point.
     *
     * @param angle The angle in degrees.
     * @param anchorX The x-coordinate of the anchor point.
     * @param anchorY The y-coordinate of the anchor point.
     */
    fun rotate(angle: Float, anchorX: Float, anchorY: Float) {
        applyPositionTransform {
            this.rotate(angle, anchorX, anchorY)
        }
    }
}

/**
 * Enum representing the gradient type of a laser point.
 */
enum class GradientType {
    Smooth, Steps, None,
}
