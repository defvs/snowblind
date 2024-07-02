package nodes

import helpers.ConnectorUUID
import helpers.NodeUUID

data class NodeConnectionUUID(val nodeUUID: NodeUUID, val connectorUUID: ConnectorUUID)

data class NodeConnection(val source: NodeConnectionUUID, val dest: NodeConnectionUUID)