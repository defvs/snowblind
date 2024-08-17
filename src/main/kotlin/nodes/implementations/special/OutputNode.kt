package nodes.implementations.special

import helpers.ConnectorUUID
import helpers.NodeUUID
import helpers.ObservablePosition
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import nodes.INodeBase
import nodes.NodeParameterMap
import nodes.parameters

@Serializable
class OutputNode(
    override val uuid: NodeUUID = NodeUUID(),
    val laserInputUUID: ConnectorUUID = ConnectorUUID(),
    override val position: ObservablePosition = ObservablePosition(),
) : INodeBase {

    override val name = "Laser Output"
    override val description = """
        Main Output of the clip.
    """.trimIndent()

    @Transient override val parameters: NodeParameterMap = parameters { }
}