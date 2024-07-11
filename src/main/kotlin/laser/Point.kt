package laser

import kotlin.math.cos
import kotlin.math.sin

data class Point(
    var x: Float,
    var y: Float,
) {
    fun offset(x: Float, y: Float) {
        this.x += x
        this.y += y
    }

    fun rotate(angle: Float, anchorX: Float, anchorY: Float) {
        if (angle % 1.0 == 0.0) return
        val radians = angle * 2 * Math.PI
        val translatedX = this.x - anchorX
        val translatedY = this.y - anchorY
        val rotatedX = translatedX * cos(radians) - translatedY * sin(radians)
        val rotatedY = translatedX * sin(radians) + translatedY * cos(radians)
        this.x = (rotatedX + anchorX).toFloat()
        this.y = (rotatedY + anchorY).toFloat()
    }
}