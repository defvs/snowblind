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

/**
 * Base sealed class representing a clip.
 */
@Serializable
sealed class Clip {
    /**
     * Name of the clip.
     */
    abstract val name: String

    /**
     * Unique identifier for the clip.
     */
    abstract val uuid: ClipUUID

    /**
     * Map of nodes associated with the clip.
     */
    val nodes = hashMapOf<NodeUUID, INodeBase>()

    /**
     * Map of connections between nodes.
     */
    val connectionMap = NodeConnectionMap()

    @Transient
    private val laserObjectCache = hashMapOf<NodeUUID, List<LaserObject>>()

    @Transient
    private val processedParamsCache = hashSetOf<NodeUUID>()

    /**
     * Adds a node to the clip.
     *
     * @param node The node to add.
     */
    operator fun plusAssign(node: INodeBase) {
        nodes[node.uuid] = node
    }

    /**
     * Adds a connection to the clip.
     *
     * @param connection The connection to add.
     */
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
                val inputLaser = inputNode?.let { processNode(it) } ?: emptyList()
                node.processLaser(inputLaser)
            }

            else -> emptyList()
        }.also {
            laserObjectCache[node.uuid] = it
        }
    }

    /**
     * Processes all nodes and returns the final output.
     *
     * @return List of processed laser objects.
     */
    open fun process(): List<LaserObject> {
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

/**
 * Class representing a generator clip.
 */
@Serializable
class GeneratorClip(
    override val name: String = "Unnamed Generator Clip",
    override val uuid: ClipUUID = ClipUUID(),
) : Clip()

/**
 * Class representing an effect clip.
 */
@Serializable
class EffectClip(
    override val name: String = "Unnamed Generator Clip",
    override val uuid: ClipUUID = ClipUUID(),
) : Clip() {
    /**
     * Processes the effect clip with the provided input laser objects.
     *
     * @param input List of input laser objects.
     * @return List of processed laser objects.
     */
    fun process(input: List<LaserObject>): List<LaserObject> {
        super.nodes.values.filterIsInstance<InputNode>()
            .ifEmpty { return emptyList() }.onEach {
                it.laserOutput = input
            }
        return super.process()
    }
}
