package nodes

import helpers.ConnectorUUID
import helpers.NodeUUID
import helpers.serialization.NodeConnectionMapSerializer
import javafx.collections.FXCollections
import javafx.collections.ObservableSet
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
 * @param connections A collection of node connections.
 */
@Serializable(with = NodeConnectionMapSerializer::class)
class NodeConnectionMap private constructor(
    private val set: ObservableSet<NodeConnection> = FXCollections.observableSet()
) : ObservableSet<NodeConnection> by set {
    constructor(vararg connections: NodeConnection): this() {
        connections.forEach(::add)
    }

    operator fun get(connector: ConnectorUUID) = this.last {
        it.dest.connectorUUID == connector || it.source.connectorUUID == connector
    }

    fun remove(connector: ConnectorUUID) = this.filter {
        it.dest.connectorUUID == connector || it.source.connectorUUID == connector
    }.forEach { this.remove(it) }
    operator fun minusAssign(connector: ConnectorUUID) { remove(connector) }

    operator fun minusAssign(connection: NodeConnection) { remove(connection) }

    fun remove(node: NodeUUID) =
        this.removeIf { it.dest.nodeUUID == node || it.source.nodeUUID == node }
    operator fun minusAssign(nodeUUID: NodeUUID) { remove(nodeUUID) }
}
