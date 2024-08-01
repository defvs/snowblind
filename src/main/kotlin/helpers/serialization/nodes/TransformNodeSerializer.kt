package helpers.serialization.nodes

import helpers.ConnectorUUID
import helpers.NodeUUID
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import nodes.NodeParameter
import nodes.TransformNode
import kotlin.reflect.KClass

@OptIn(ExperimentalSerializationApi::class)
abstract class TransformNodeSerializer<T : TransformNode>(
    private val createNode: (Surrogate) -> T,
    private val `class`: KClass<T>,
) : KSerializer<T> {
    @Serializable
    data class Surrogate(
        val uuid: NodeUUID,
        val laserInputUUID: ConnectorUUID,
        val laserOutputUUID: ConnectorUUID,
        val parametersUUIDs: List<ConnectorUUID>,
        val internalParametersValues: Map<ConnectorUUID, Float>,
    )

    override val descriptor: SerialDescriptor
        get() = SerialDescriptor(
            `class`.simpleName!!,
            GeneratorNodeSerializer.Surrogate.serializer().descriptor
        )

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeSerializableValue(Surrogate.serializer(), Surrogate(
            value.uuid,
            value.laserInputUUID,
            value.laserOutputUUID,
            value.parameters.parameters.map { it.uuid },
            value.parameters.parameters
                .filterIsInstance<NodeParameter.ControllableParameter>()
                .associate { it.uuid to it.control.value.get() }
        ))
    }

    override fun deserialize(decoder: Decoder): T {
        val surrogate = decoder.decodeSerializableValue(Surrogate.serializer())
        return createNode(surrogate)
    }
}
