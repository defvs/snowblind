package nodes.helpers

import kotlin.math.roundToInt

interface ValueConverter {
    fun toString(value: Float): String
    fun fromString(string: String): Float?
}

object SimpleValueConverters {
    object AsInteger : ValueConverter {
        override fun toString(value: Float) = value.roundToInt().toString()
        override fun fromString(string: String) = string.trim().toFloatOrNull()
    }

    class AsDecimal(private val digits: Int = 2) : ValueConverter {
        override fun toString(value: Float) = String.format("%.${digits}d", value)
        override fun fromString(string: String) = string.trim().toFloatOrNull()
    }

    object AsDegrees : ValueConverter {
        private const val DEGREES = '\u00B0'
        override fun toString(value: Float) = value.times(360).roundToInt().toString() + DEGREES
        override fun fromString(string: String) =
            string.trim().removeSuffix(DEGREES.toString()).toFloatOrNull()?.div(360)
    }

    object As8bitColor : ValueConverter {
        override fun toString(value: Float) = value.times(256).roundToInt().toString()

        override fun fromString(string: String) = string.toFloatOrNull()?.div(256)
    }
}

