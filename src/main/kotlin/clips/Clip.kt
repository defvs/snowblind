package clips

import helpers.ClipUUID
import helpers.NodeUUID
import laser.LaserObject
import nodes.*
import nodes.implementations.special.MacroNode
import nodes.implementations.special.OutputNode

class Clip(val uuid: ClipUUID = ClipUUID()) {
    private val nodes = hashMapOf<NodeUUID, Node>()
    private val connectionMap = NodeConnectionMap()

    operator fun plusAssign(node: Node) {
        nodes[node.uuid] = node
    }

    operator fun plusAssign(connection: NodeConnection) {
        connectionMap.addConnection(connection)
    }

    private fun processParameters(node: Node) {
        if (node is INodeHasInputParams) {
            for (param in node.inputParams) {
                val connection = connectionMap.getConnectionByConnector(param.uuid)
                val sourceNode = connection?.source?.nodeUUID?.let { nodes[it] }
                if (sourceNode is INodeHasOutputParams) {
                    val sourceParam = sourceNode.outputParams[param.uuid]
                    if (sourceParam != null) {
                        when (sourceNode) {
                            is ParameterTransformNode -> sourceNode.processParameter()
                            is MacroNode -> return
                        }
                        node.inputParams[param.uuid] = sourceParam.data
                    }
                }
            }
        }
    }

    private fun processNode(node: Node): List<LaserObject> {
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
        }
    }

    fun process(): List<LaserObject> {
        val outputNodes = nodes.values.filterIsInstance<OutputNode>().ifEmpty { return emptyList() }

        val laserObjects = mutableListOf<LaserObject>()

        for (outputNode in outputNodes) {
            val inputConnection = connectionMap.getConnectionByConnector(outputNode.laserInputUUID)
            val inputNode = inputConnection?.source?.nodeUUID?.let { nodes[it] }
            if (inputNode != null) {
                laserObjects.addAll(processNode(inputNode))
            }
        }

        return laserObjects
    }
}
