package clips

import helpers.ClipUUID
import helpers.NodeUUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import laser.LaserObject
import nodes.*
import nodes.implementations.special.InputNode
import nodes.implementations.special.MacroNode
import nodes.implementations.special.OutputNode

@Serializable
sealed class Clip {
    abstract val name: String
    abstract val uuid: ClipUUID
    val nodes = hashMapOf<NodeUUID, INodeBase>()
    val connectionMap = NodeConnectionMap()

    @Transient
    private val laserObjectCache = hashMapOf<NodeUUID, List<LaserObject>>()
    @Transient
    private val processedParamsCache = hashSetOf<NodeUUID>()

    operator fun plusAssign(node: INodeBase) {
        nodes[node.uuid] = node
    }

    operator fun plusAssign(connection: NodeConnection) {
        connectionMap.addConnection(connection)
    }

    private fun processParameters(node: INodeBase) {
        if (node !is INodeHasInputParams || processedParamsCache.contains(node.uuid)) return
        node.inputParams.forEach { param ->
            val sourceNode = connectionMap
                .getConnectionByConnector(param.uuid)?.source?.nodeUUID?.let { nodes[it] }
            if (sourceNode !is INodeHasOutputParams) return@forEach
            val sourceParam = sourceNode.outputParams[param.uuid] ?: return@forEach
            when (sourceNode) {
                is ParameterTransformNode -> sourceNode.processParameter()
                is MacroNode -> return
                else -> return
            }
            node.inputParams[param.uuid] = sourceParam.data
            processedParamsCache += node.uuid
        }
    }

    private fun processNode(node: INodeBase): List<LaserObject> {
        laserObjectCache[node.uuid]?.let { return it }
        processParameters(node)
        return when (node) {
            is GeneratorNode -> node.laserOutput
            is TransformNode -> {
                val inputConnection = connectionMap.getConnectionByConnector(node.laserInputUUID)
                val inputNode = inputConnection?.source?.nodeUUID?.let { nodes[it] }
                val inputLaserObjects = inputNode?.let { processNode(it) } ?: emptyList()
                node.processLaser(inputLaserObjects)
            }


            else -> emptyList()
        }.also { laserObjectCache[node.uuid] = it }
    }

    protected open fun process(): List<LaserObject> {
        laserObjectCache.clear()
        processedParamsCache.clear()

        val outputNodes = nodes.values.filterIsInstance<OutputNode>().ifEmpty { return emptyList() }

        val laserObjects = mutableListOf<LaserObject>()

        for (outputNode in outputNodes) {
            val inputConnection = connectionMap.getConnectionByConnector(outputNode.laserInputUUID)
            val inputNode = inputConnection?.source?.nodeUUID?.let { nodes[it] }
            if (inputNode != null) {
                laserObjects.addAll(processNode(inputNode))
            }
        }

        laserObjectCache.clear()
        processedParamsCache.clear()

        return laserObjects
    }
}

@Serializable
class GeneratorClip(
    override val name: String = "Unnamed Generator Clip",
    override val uuid: ClipUUID = ClipUUID()
) : Clip() {
    public override fun process() = super.process()
}

@Serializable
class EffectClip(
    override val name: String = "Unnamed Generator Clip",
    override val uuid: ClipUUID = ClipUUID()
) : Clip() {
    fun process(input: List<LaserObject>): List<LaserObject> {
        super.nodes.values.filterIsInstance<InputNode>()
            .ifEmpty { return emptyList() }.onEach {
                it.laserOutput = input
            }
        return super.process()
    }
}