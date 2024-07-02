package nodes.implementations.special

import nodes.*

class MacroNode : Node(
    name = "Macro Node"
), INodeHasOutputParams {
    override val outputParams = NodeParameterMap(
        NodeParameter(ParameterType.Generic)
    )
}