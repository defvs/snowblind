package laser

data class Color(
    val red: Double,
    val green: Double,
    val blue: Double,
    val opacity: Double = 1.0,
) {
    var hue : Double
        get() = TODO()
        set(value) = TODO()
    var saturation : Double
        get() = TODO()
        set(value) = TODO()
}