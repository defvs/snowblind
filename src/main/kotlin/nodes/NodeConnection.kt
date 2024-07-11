package nodes

import helpers.ConnectorUUID
import helpers.NodeUUID
import helpers.serialization.NodeConnectionMapSerializer
import kotlinx.serialization.Serializable

@Serializable
data class NodeConnectionUUID(val nodeUUID: NodeUUID, val connectorUUID: ConnectorUUID)

@Serializable
data class NodeConnection(val source: NodeConnectionUUID, val dest: NodeConnectionUUID) {
    constructor(
        sourceNodeUUID: NodeUUID, sourceConnectorUUID: ConnectorUUID,
        destNodeUUID: NodeUUID, destConnectorUUID: ConnectorUUID
    ) : this(
        NodeConnectionUUID(sourceNodeUUID, sourceConnectorUUID),
        NodeConnectionUUID(destNodeUUID, destConnectorUUID)
    )
}

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