package laser

import kotlin.math.cos
import kotlin.math.sin

data class Point(
    var x: Double,
    var y: Double,
) {
    fun offset(x: Double, y: Double) {
        this.x += x
        this.y += y
    }

    fun rotate(angle: Double, anchorX: Double, anchorY: Double) {
        if (angle % 1.0 == 0.0) return
        val radians = angle * 2 * Math.PI
        val translatedX = this.x - anchorX
        val translatedY = this.y - anchorY
        val rotatedX = translatedX * cos(radians) - translatedY * sin(radians)
        val rotatedY = translatedX * sin(radians) + translatedY * cos(radians)
        this.x = rotatedX + anchorX
        this.y = rotatedY + anchorY
    }
}