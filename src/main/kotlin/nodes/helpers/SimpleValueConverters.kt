package nodes.helpers

import kotlin.math.roundToInt

object SimpleValueConverters {
    val asInteger: (Float) -> String = { it.roundToInt().toString() }
    fun asDecimal(digits: Int): (Float) -> String = { String.format("%.${digits}d", it) }
    val asDegrees: (Float) -> String = { it.times(360).roundToInt().toString() }
    val as8bitColor: (Float) -> String = { it.times(256).roundToInt().toString() }
}

