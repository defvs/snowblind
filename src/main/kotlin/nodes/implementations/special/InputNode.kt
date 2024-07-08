package nodes.implementations.special

import laser.LaserObject
import nodes.GeneratorNode

class InputNode : GeneratorNode(
    name = "Laser Input",
    description = null,
) {
    override var laserOutput: List<LaserObject> = emptyList()
}