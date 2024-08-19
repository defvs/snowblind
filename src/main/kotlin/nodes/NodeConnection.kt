package nodes

import helpers.ConnectorUUID
import helpers.NodeUUID
import helpers.serialization.NodeConnectionMapSerializer
import javafx.collections.FXCollections
import javafx.collections.ObservableMap
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

    operator fun contains(connectorUUID: ConnectorUUID) =
        source.connectorUUID == connectorUUID || dest.connectorUUID == connectorUUID

    operator fun contains(nodeUUID: NodeUUID) =
        source.nodeUUID == nodeUUID || dest.nodeUUID == nodeUUID
}

/**
 * Class representing a map of node connections.
 *
 * @property connections A collection of node connections.
 */
@Serializable(with = NodeConnectionMapSerializer::class)
class NodeConnectionMap private constructor(
    private val map: ObservableMap<ConnectorUUID, NodeConnection> = FXCollections.observableHashMap()
) : ObservableMap<ConnectorUUID, NodeConnection> by map {
    constructor(vararg connections: NodeConnection): this() {
        connections.forEach(::add)
    }

    val connections: Collection<NodeConnection>
        get() = this.values

    fun add(connection: NodeConnection) {
        this[connection.source.connectorUUID] = connection
        this[connection.dest.connectorUUID] = connection
    }
    operator fun plusAssign(connection: NodeConnection) = add(connection)

    override fun remove(key: ConnectorUUID) = this.values.firstOrNull {
        it.dest.connectorUUID == key || it.source.connectorUUID == key
    }?.also { this.values.remove(it) }
    operator fun minusAssign(connector: ConnectorUUID) { remove(connector) }

    fun remove(connection: NodeConnection) {
        remove(connection.source.connectorUUID)
        remove(connection.dest.connectorUUID)
    }
    operator fun minusAssign(connection: NodeConnection) = remove(connection)

    fun remove(nodeUUID: NodeUUID) =
        this.values.removeIf { it.dest.nodeUUID == nodeUUID || it.source.nodeUUID == nodeUUID }
    operator fun minusAssign(nodeUUID: NodeUUID) { remove(nodeUUID) }
}
