package nodes

import laser.LaserObject
import java.util.*

abstract class Node(
    val name: String,
    val description: String? = null,
    val uuid: UUID = UUID.randomUUID(),
)

interface INodeHasInputParams {
    val inputParams: NodeParameterMap
}

interface INodeHasOutputParams {
    val outputParams: NodeParameterMap
}

abstract class GeneratorNode(
    name: String,
    description: String? = null,
    val laserOutputUUID: UUID = UUID.randomUUID(),
) : Node(name, description) {
    abstract val laserOutput: List<LaserObject>
}

abstract class TransformNode(
    name: String,
    description: String? = null,
    val laserInputUUID: UUID = UUID.randomUUID(),
    val laserOutputUUID: UUID = UUID.randomUUID(),
) : Node(name, description) {
    abstract fun processLaser(input: List<LaserObject>): List<LaserObject>
}

abstract class ParameterTransformNode(name: String, description: String? = null) : Node(name, description),
    INodeHasInputParams, INodeHasOutputParams {
    abstract fun processParameter()
}
