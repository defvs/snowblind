package nodes.implementations.special

import helpers.ConnectorUUID
import helpers.NodeUUID
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import nodes.INodeBase
import nodes.INodeHasInputLaser

@Serializable
class OutputNode : INodeBase, INodeHasInputLaser {
    @Transient override val name = "Laser Output"

    @Transient override val description = """
        Main Output of the clip.
    """.trimIndent()

    @Required override val uuid: NodeUUID = NodeUUID()

    @Required override val laserInputUUID: ConnectorUUID = ConnectorUUID()
}