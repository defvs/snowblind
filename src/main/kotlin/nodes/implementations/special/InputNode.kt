@file:UseSerializers(
    FloatPropertySerializer::class
)

package nodes.implementations.special

import helpers.ConnectorUUID
import helpers.NodeUUID
import helpers.ObservablePosition
import helpers.OnlyFXClipNode
import helpers.serialization.javafx.FloatPropertySerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import nodes.GeneratorNode
import nodes.NodeParameterMap
import nodes.parameters

@OnlyFXClipNode
@Serializable
class InputNode(
    override val uuid: NodeUUID = NodeUUID(),
    override val laserOutputUUID: ConnectorUUID = ConnectorUUID(),
    override val position: ObservablePosition = ObservablePosition()
) : GeneratorNode {
    override val name = "Laser Input"
    override val description = null

    @Transient override val parameters: NodeParameterMap = parameters { }

    override fun computeLaser(inputParameters: Map<ConnectorUUID, Float>) =
        throw Exception("computeLaser run on special node type InputNode.")
}