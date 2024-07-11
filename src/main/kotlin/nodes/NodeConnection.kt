package nodes

import helpers.ConnectorUUID
import helpers.NodeUUID
import helpers.serialization.NodeConnectionMapSerializer
import kotlinx.serialization.Serializable

/**
 * Data class representing a connection between a node and a connector.
 */
@Serializable
data class NodeConnectionUUID(
    val nodeUUID: NodeUUID,
    val connectorUUID: ConnectorUUID,
)

/**
 * Data class representing a connection from a source to a destination node.
 */
@Serializable
data class NodeConnection(
    val source: NodeConnectionUUID,
    val dest: NodeConnectionUUID,
) {
    constructor(
        sourceNodeUUID: NodeUUID,
        sourceConnectorUUID: ConnectorUUID,
        destNodeUUID: NodeUUID,
        destConnectorUUID: ConnectorUUID,
    ) : this(
        NodeConnectionUUID(sourceNodeUUID, sourceConnectorUUID),
        NodeConnectionUUID(destNodeUUID, destConnectorUUID)
    )
}

/**
 * Class representing a map of node connections.
 *
 * @property connections A collection of node connections.
 */
@Serializable(with = NodeConnectionMapSerializer::class)
class NodeConnectionMap(vararg connections: NodeConnection) {
    private val connectionsByNode = hashMapOf<NodeUUID, MutableList<NodeConnection>>()
    private val connectionsByConnector = hashMapOf<ConnectorUUID, NodeConnection>()

    val connections: Collection<NodeConnection>
        get() = connectionsByConnector.values

    init {
        connections.forEach(::addConnection)
    }

    /**
     * Adds a connection to the map.
     *
     * @param connection The connection to add.
     */
    fun addConnection(connection: NodeConnection) {
        connectionsByNode.computeIfAbsent(connection.source.nodeUUID) { mutableListOf() }.add(connection)
        connectionsByNode.computeIfAbsent(connection.dest.nodeUUID) { mutableListOf() }.add(connection)
        connectionsByConnector[connection.source.connectorUUID] = connection
        connectionsByConnector[connection.dest.connectorUUID] = connection
    }

    operator fun plusAssign(connection: NodeConnection) = addConnection(connection)

    /**
     * Gets connections by the node UUID.
     *
     * @param nodeUUID The UUID of the node.
     * @return List of connections related to the node.
     */
    fun getConnectionsByNode(nodeUUID: NodeUUID): List<NodeConnection> {
        return connectionsByNode[nodeUUID] ?: emptyList()
    }

    /**
     * Gets a connection by the connector UUID.
     *
     * @param connectorUUID The UUID of the connector.
     * @return The connection associated with the connector, or null if none exists.
     */
    fun getConnectionByConnector(connectorUUID: ConnectorUUID): NodeConnection? {
        return connectionsByConnector[connectorUUID]
    }
}
