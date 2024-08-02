package nodes

import helpers.ConnectorUUID
import helpers.NodeUUID
import laser.LaserObject

/**
 * Base interface for all nodes.
 */
interface INodeBase {
    val name: String
    val description: String?
    val uuid: NodeUUID
    val parameters: NodeParameterMap
}

/**
 * Abstract class representing a generator node.
 */
interface GeneratorNode : INodeBase {
    override val name: String
    override val description: String?
    val laserOutputUUID: ConnectorUUID
    fun computeLaser(
        inputParameters: Map<ConnectorUUID, Float>,
    ): List<LaserObject>
}

/**
 * Abstract class representing a transform node.
 */
interface TransformNode : INodeBase {
    val laserInputUUID: ConnectorUUID
    val laserOutputUUID: ConnectorUUID
    fun transformLaser(
        inputLaser: List<LaserObject>,
        inputParameters: Map<ConnectorUUID, Float>,
    ): List<LaserObject>
}

/**
 * Abstract class representing a parameter transform node.
 */
interface ParameterTransformNode : INodeBase {
    fun processParameters(
        inputParameters: Map<ConnectorUUID, Float>,
    ): Map<ConnectorUUID, Float>
}

interface ParameterGeneratorNode : INodeBase {
    fun computeParameter(
        inputParameters: Map<ConnectorUUID, Float>,
    ): Map<ConnectorUUID, Float>
}