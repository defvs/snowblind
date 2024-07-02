package nodes

import helpers.NodeUUID
import helpers.ConnectorUUID

class NodeConnectionMap {
    private val connectionsByNode = hashMapOf<NodeUUID, MutableList<NodeConnection>>()
    private val connectionsByConnector = hashMapOf<ConnectorUUID, NodeConnection>()

    fun addConnection(connection: NodeConnection) {
        connectionsByNode.computeIfAbsent(connection.source.nodeUUID) { mutableListOf() }.add(connection)
        connectionsByNode.computeIfAbsent(connection.dest.nodeUUID) { mutableListOf() }.add(connection)
        connectionsByConnector[connection.source.connectorUUID] = connection
        connectionsByConnector[connection.dest.connectorUUID] = connection
    }

    fun getConnectionsByNode(nodeUUID: NodeUUID): List<NodeConnection> {
        return connectionsByNode[nodeUUID] ?: emptyList()
    }

    fun getConnectionByConnector(connectorUUID: ConnectorUUID): NodeConnection? {
        return connectionsByConnector[connectorUUID]
    }
}
