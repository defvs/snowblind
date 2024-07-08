package nodes.implementations.special

import helpers.OnlyFXClipNode
import laser.LaserObject
import nodes.GeneratorNode

@OnlyFXClipNode
class InputNode : GeneratorNode(
    name = "Laser Input",
    description = null,
) {
    override var laserOutput: List<LaserObject> = emptyList()
}