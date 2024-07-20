package nodes

import helpers.ConnectorUUID
import helpers.NodeUUID
import kotlinx.serialization.Serializable
import laser.LaserObject

/**
 * Base interface for all nodes.
 */
interface INodeBase {
    val name: String
    val description: String?
    val uuid: NodeUUID
}

/**
 * Interface for nodes with input parameters.
 */
interface INodeHasInputParams {
    val inputParams: NodeParameterMap
}

/**
 * Interface for nodes with output parameters.
 */
interface INodeHasOutputParams {
    val outputParams: NodeParameterMap
}

interface INodeHasInternalParams {
    val internalParams: NodeParameterMap
}

/**
 * Interface for nodes with laser input.
 */
interface INodeHasInputLaser {
    val laserInputUUID: ConnectorUUID
}

/**
 * Interface for nodes with laser output.
 */
interface INodeHasOutputLaser {
    val laserOutputUUID: ConnectorUUID
}

/**
 * Abstract class representing a generator node.
 */
@Serializable
abstract class GeneratorNode(
    override val name: String,
    override val description: String? = null,
) : INodeBase, INodeHasOutputLaser {
    abstract val laserOutput: List<LaserObject>
}

/**
 * Abstract class representing a transform node.
 */
@Serializable
abstract class TransformNode(
    override val name: String,
    override val description: String? = null,
) : INodeBase, INodeHasInputLaser, INodeHasOutputLaser {
    abstract fun processLaser(input: List<LaserObject>): List<LaserObject>
}

/**
 * Abstract class representing a parameter transform node.
 */
@Serializable
abstract class ParameterTransformNode(
    override val name: String,
    override val description: String? = null,
) : INodeBase, INodeHasInputParams, INodeHasOutputParams {
    abstract fun processParameter()
}
