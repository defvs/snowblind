package nodes

import helpers.ConnectorUUID
import helpers.NodeUUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import laser.LaserObject
import nodes.implementations.generators.PointGeneratorNode
import nodes.implementations.special.InputNode
import nodes.implementations.special.MacroNode
import nodes.implementations.special.OutputNode
import nodes.implementations.transforms.HSVShiftNode
import nodes.implementations.transforms.PositionOffsetTransformNode

val nodeSerializerModule = SerializersModule {
    polymorphic(Node::class) {
        subclass(PointGeneratorNode::class)
        subclass(InputNode::class)
        subclass(MacroNode::class)
        subclass(OutputNode::class)
        subclass(HSVShiftNode::class)
        subclass(PositionOffsetTransformNode::class)
    }
}

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
