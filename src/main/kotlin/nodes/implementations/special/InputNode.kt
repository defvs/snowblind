package nodes.implementations.special

import helpers.ConnectorUUID
import helpers.NodeUUID
import helpers.OnlyFXClipNode
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import laser.LaserObject
import nodes.GeneratorNode

@OnlyFXClipNode
@Serializable
class InputNode(
    override val uuid: NodeUUID = NodeUUID(),
    override val laserOutputUUID: ConnectorUUID = ConnectorUUID()
) : GeneratorNode(
    name = "Laser Input",
    description = null,
) {
    @Transient override var laserOutput: List<LaserObject> = emptyList()
}