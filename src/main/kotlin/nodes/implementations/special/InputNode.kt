package nodes.implementations.special

import helpers.OnlyFXClipNode
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import laser.LaserObject
import nodes.GeneratorNode

@OnlyFXClipNode
@Serializable
class InputNode : GeneratorNode(
    name = "Laser Input",
    description = null,
) {
    @Transient override var laserOutput: List<LaserObject> = emptyList()
}