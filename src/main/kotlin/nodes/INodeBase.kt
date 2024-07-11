package nodes

import helpers.ConnectorUUID
import helpers.NodeUUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import laser.LaserObject

interface INodeBase {
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
) : INodeBase, INodeHasOutputLaser {
    abstract val laserOutput: List<LaserObject>
}

@Serializable
abstract class TransformNode(
    override val uuid: NodeUUID = NodeUUID(),
    override val name: String,
    override val description: String? = null,
    override val laserInputUUID: ConnectorUUID = ConnectorUUID(),
    override val laserOutputUUID: ConnectorUUID = ConnectorUUID(),
) : INodeBase, INodeHasInputLaser, INodeHasOutputLaser {
    abstract fun processLaser(input: List<LaserObject>): List<LaserObject>
}

@Serializable
abstract class ParameterTransformNode(
    override val uuid: NodeUUID = NodeUUID(),
    override val name: String,
    override val description: String? = null
) : INodeBase, INodeHasInputParams, INodeHasOutputParams {
    abstract fun processParameter()
}
