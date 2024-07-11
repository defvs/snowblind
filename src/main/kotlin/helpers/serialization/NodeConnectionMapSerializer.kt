package helpers.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import nodes.NodeConnection
import nodes.NodeConnectionMap

class NodeConnectionMapSerializer : KSerializer<NodeConnectionMap> {
    @OptIn(ExperimentalSerializationApi::class)
    private val delegateSerializer = ArraySerializer(NodeConnection.serializer())
    override val descriptor = delegateSerializer.descriptor

    override fun deserialize(decoder: Decoder) =
        NodeConnectionMap(*decoder.decodeSerializableValue(delegateSerializer))

    override fun serialize(encoder: Encoder, value: NodeConnectionMap) {
        encoder.encodeSerializableValue(delegateSerializer, value.connections.toTypedArray())
    }

}