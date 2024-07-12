package nodes.implementations.special

import helpers.NodeUUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import nodes.*

@Serializable
class MacroNode : INodeBase, INodeHasOutputParams {
    @Transient override val name = "Macro Node"
    @Transient override val description = """
        Parameter Input from your DAW.
    """.trimIndent()
    override val uuid: NodeUUID = NodeUUID()

    override val outputParams = NodeParameterMap(
        NodeParameter(ParameterType.Index, data = 1.0f, isExposed = false),
        NodeParameter(ParameterType.Generic)
    )
}