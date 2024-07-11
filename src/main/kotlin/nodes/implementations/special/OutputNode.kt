package nodes.implementations.special

import helpers.ConnectorUUID
import helpers.NodeUUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import nodes.INodeHasInputLaser
import nodes.Node

@Serializable
class OutputNode : Node, INodeHasInputLaser {
    @Transient
    override val name = "Laser Output"
    @Transient
    override val description = """
        Main Output of the clip.
    """.trimIndent()
    override val uuid: NodeUUID = NodeUUID()

    override val laserInputUUID: ConnectorUUID = ConnectorUUID()
}