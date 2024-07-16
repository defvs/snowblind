package laser

import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Represents a 2D point.
 *
 * @property x The x-coordinate of the point.
 * @property y The y-coordinate of the point.
 */
data class Point(
    var x: Float,
    var y: Float,
) {
    /**
     * Offsets the point by the given x and y values.
     *
     * @param x The x-offset.
     * @param y The y-offset.
     */
    fun offset(x: Float, y: Float) {
        this.x += x
        this.y += y
    }

    /**
     * Rotates the point around a given anchor point by a specified angle.
     *
     * @param angle The angle in degrees.
     * @param anchorX The x-coordinate of the anchor point.
     * @param anchorY The y-coordinate of the anchor point.
     */
    fun rotate(angle: Float, anchorX: Float, anchorY: Float) {
        if (angle % 1.0 == 0.0) return
        val radians = angle * 2 * Math.PI / 360.0
        val translatedX = this.x - anchorX
        val translatedY = this.y - anchorY
        val rotatedX = translatedX * cos(radians) - translatedY * sin(radians)
        val rotatedY = translatedX * sin(radians) + translatedY * cos(radians)
        this.x = (rotatedX + anchorX).toFloat()
        this.y = (rotatedY + anchorY).toFloat()
    }

    /**
     * Reflects the point across the X-axis.
     */
    fun axialSymmetryX() {
        this.y = -this.y
    }

    /**
     * Reflects the point across the Y-axis.
     */
    fun axialSymmetryY() {
        this.x = -this.x
    }

    /**
     * Offsets the point randomly within the given bounds.
     *
     * @param maxX The maximum x-offset.
     * @param maxY The maximum y-offset.
     */
    fun randomOffset(maxX: Float, maxY: Float) {
        this.x += Random.nextFloat() * maxX - maxX / 2
        this.y += Random.nextFloat() * maxY - maxY / 2
    }

    /**
     * Reflects the point across an arbitrary axis passing through the origin.
     *
     * @param angle The angle in degrees defining the axis from the positive x-axis.
     */
    fun axialSymmetry(angle: Float) {
        val radians = angle * 2 * Math.PI / 360.0
        val cosTheta = cos(radians)
        val sinTheta = sin(radians)
        val newX = this.x * cosTheta + this.y * sinTheta
        val newY = this.x * sinTheta - this.y * cosTheta
        this.x = (newX * cosTheta + newY * sinTheta).toFloat()
        this.y = (newX * sinTheta - newY * cosTheta).toFloat()
    }
}
