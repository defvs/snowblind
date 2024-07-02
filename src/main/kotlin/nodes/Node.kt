package nodes

import helpers.ConnectorUUID
import helpers.NodeUUID
import laser.LaserObject

abstract class Node(
    val name: String,
    val description: String? = null,
    val uuid: NodeUUID = NodeUUID(),
)

interface INodeHasInputParams {
    val inputParams: NodeParameterMap
}

interface INodeHasOutputParams {
    val outputParams: NodeParameterMap
}

interface INodeHasInputLaser {
    val laserInputUUID: ConnectorUUID
}

interface INodeHasOutputLaser {
    val laserOutputUUID: ConnectorUUID
}

abstract class GeneratorNode(
    name: String,
    description: String? = null,
    override val laserOutputUUID: ConnectorUUID = ConnectorUUID()
) : Node(name, description), INodeHasOutputLaser {
    abstract val laserOutput: List<LaserObject>
}

abstract class TransformNode(
    name: String,
    description: String? = null,
    override val laserInputUUID: ConnectorUUID = ConnectorUUID(),
    override val laserOutputUUID: ConnectorUUID = ConnectorUUID(),
) : Node(name, description), INodeHasInputLaser, INodeHasOutputLaser {
    abstract fun processLaser(input: List<LaserObject>): List<LaserObject>
}

abstract class ParameterTransformNode(name: String, description: String? = null) : Node(name, description),
    INodeHasInputParams, INodeHasOutputParams {
    abstract fun processParameter()
}
