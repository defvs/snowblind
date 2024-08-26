package nodes

interface ValueConverter {
    fun toString(value: Float): String
    fun fromString(string: String): Float?
}