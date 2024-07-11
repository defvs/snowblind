package helpers.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import nodes.NodeParameter
import nodes.NodeParameterMap

class NodeParameterMapSerializer : KSerializer<NodeParameterMap> {
    @OptIn(ExperimentalSerializationApi::class)
    private val delegateSerializer = ArraySerializer(NodeParameter.serializer())
    override val descriptor = delegateSerializer.descriptor

    override fun deserialize(decoder: Decoder) =
        NodeParameterMap(*decoder.decodeSerializableValue(delegateSerializer))

    override fun serialize(encoder: Encoder, value: NodeParameterMap) =
        encoder.encodeSerializableValue(delegateSerializer, value.parameters.toTypedArray())

}