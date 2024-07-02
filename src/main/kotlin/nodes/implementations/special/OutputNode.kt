package nodes.implementations.special

import helpers.ConnectorUUID
import nodes.INodeHasInputLaser
import nodes.Node

class OutputNode : Node(
    name = "Laser Output",
    description = null,
), INodeHasInputLaser {
    override val laserInputUUID: ConnectorUUID = ConnectorUUID()
}