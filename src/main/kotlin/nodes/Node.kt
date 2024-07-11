package nodes

import helpers.ConnectorUUID
import helpers.NodeUUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import laser.LaserObject

interface Node {
    @Transient
    val name: String
    @Transient
    val description: String?
    val uuid: NodeUUID
}

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

@Serializable
abstract class GeneratorNode(
    override val uuid: NodeUUID = NodeUUID(),
    override val name: String,
    override val description: String? = null,
    override val laserOutputUUID: ConnectorUUID = ConnectorUUID()
) : Node, INodeHasOutputLaser {
    abstract val laserOutput: List<LaserObject>
}

@Serializable
abstract class TransformNode(
    override val uuid: NodeUUID = NodeUUID(),
    override val name: String,
    override val description: String? = null,
    override val laserInputUUID: ConnectorUUID = ConnectorUUID(),
    override val laserOutputUUID: ConnectorUUID = ConnectorUUID(),
) : Node, INodeHasInputLaser, INodeHasOutputLaser {
    abstract fun processLaser(input: List<LaserObject>): List<LaserObject>
}

@Serializable
abstract class ParameterTransformNode(
    override val uuid: NodeUUID = NodeUUID(),
    override val name: String,
    override val description: String? = null
) : Node, INodeHasInputParams, INodeHasOutputParams {
    abstract fun processParameter()
}
