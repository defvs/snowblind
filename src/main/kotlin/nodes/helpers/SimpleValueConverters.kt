package nodes.helpers

import kotlin.math.roundToInt

typealias ReadableValueConverter = (Float) -> String

object SimpleValueConverters {
    val asInteger: ReadableValueConverter = { it.roundToInt().toString() }
    fun asDecimal(digits: Int): ReadableValueConverter = { String.format("%.${digits}d", it) }
    val asDegrees: ReadableValueConverter = { it.times(360).roundToInt().toString() + '\u00B0' }
    val as8bitColor: ReadableValueConverter = { it.times(256).roundToInt().toString() }
}

