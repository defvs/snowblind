package nodes.implementations.special

import helpers.NodeUUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import nodes.*

@Serializable
class MacroNode : Node, INodeHasOutputParams {
    @Transient override val name = "Macro Node"
    @Transient override val description = """
        Parameter Input from your DAW.
    """.trimIndent()
    override val uuid: NodeUUID = NodeUUID()

    override val outputParams = NodeParameterMap(
        NodeParameter(ParameterType.Generic)
    )
}