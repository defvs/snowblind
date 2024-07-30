package nodes.implementations.special

import helpers.NodeUUID
import helpers.ConnectorUUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import nodes.INodeBase
import nodes.NodeParameterMap
import nodes.parameters

@Serializable
class OutputNode(
    override val uuid: NodeUUID = NodeUUID(),
    val laserInputUUID: ConnectorUUID = ConnectorUUID(),
) : INodeBase {

    override val name = "Laser Output"
    override val description = """
        Main Output of the clip.
    """.trimIndent()

    @Transient override val parameters: NodeParameterMap = parameters { }
}