package laser

abstract class LaserObject(val points: List<Point>)
class LaserPoint(val point: Point, val color: Color) : LaserObject(points = listOf(point))
class LaserPolygon(points: List<Point>, closed: Boolean) : LaserObject(points) // todo colors