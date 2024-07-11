package nodes.implementations.special

import helpers.NodeUUID
import nodes.*

class MacroNode : Node, INodeHasOutputParams {
    override val name = "Macro Node"
    override val description = """
        Parameter Input from your DAW.
    """.trimIndent()
    override val uuid: NodeUUID = NodeUUID()

    override val outputParams = NodeParameterMap(
        NodeParameter(ParameterType.Generic)
    )
}