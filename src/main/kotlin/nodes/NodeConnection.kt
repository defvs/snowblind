package nodes

import helpers.ConnectorUUID
import helpers.NodeUUID
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