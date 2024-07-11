package nodes

import helpers.ConnectorUUID
import helpers.NodeUUID
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = NodeConnectionMapSerializer::class)
class NodeConnectionMap(vararg connections: NodeConnection) {
    private val connectionsByNode = hashMapOf<NodeUUID, MutableList<NodeConnection>>()
    private val connectionsByConnector = hashMapOf<ConnectorUUID, NodeConnection>()

    val connections: Collection<NodeConnection>
        get() = connectionsByConnector.values

    init {
        connections.forEach(::addConnection)
    }

    fun addConnection(connection: NodeConnection) {
        connectionsByNode.computeIfAbsent(connection.source.nodeUUID) { mutableListOf() }.add(connection)
        connectionsByNode.computeIfAbsent(connection.dest.nodeUUID) { mutableListOf() }.add(connection)
        connectionsByConnector[connection.source.connectorUUID] = connection
        connectionsByConnector[connection.dest.connectorUUID] = connection
    }

    operator fun plusAssign(connection: NodeConnection) = addConnection(connection)

    fun getConnectionsByNode(nodeUUID: NodeUUID): List<NodeConnection> {
        return connectionsByNode[nodeUUID] ?: emptyList()
    }

    fun getConnectionByConnector(connectorUUID: ConnectorUUID): NodeConnection? {
        return connectionsByConnector[connectorUUID]
    }
}

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
