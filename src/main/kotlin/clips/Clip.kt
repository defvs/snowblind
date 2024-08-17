@file:UseSerializers(
    StringPropertySerializer::class,
    ObjectPropertySerializer::class,
)

package clips

import helpers.ClipUUID
import helpers.ConnectorUUID
import helpers.NodeUUID
import helpers.serialization.javafx.ObjectPropertySerializer
import helpers.serialization.javafx.ObservableMapSerializer
import helpers.serialization.javafx.StringPropertySerializer
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
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
    abstract val name: StringProperty

    /**
     * Unique identifier for the clip.
     */
    val uuid: ObjectProperty<ClipUUID> = SimpleObjectProperty(ClipUUID())

    /**
     * Map of nodes associated with the clip.
     */
    @Serializable(with = ObservableMapSerializer::class)
    val nodes = FXCollections.observableHashMap<NodeUUID, INodeBase>()!!

    /**
     * Map of connections between nodes.
     */
    val connectionMap = NodeConnectionMap()

    @Transient protected val laserOutputCache = hashMapOf<NodeUUID, List<LaserObject>>()

    @Transient protected val paramsCache = hashMapOf<ConnectorUUID, Float>()

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
        connectionMap.add(connection)
    }

    private fun processParameterNode(nodeUUID: NodeUUID) =
        nodes[nodeUUID]?.let { processParameterNode(it) } ?: emptyMap()

    private fun processParameterNode(node: INodeBase): Map<ConnectorUUID, Float> {
        node.parameters.parameters.filterIsInstance<NodeParameter.OutputParameter>().let { parameters ->
            if (parameters.none { it.uuid in paramsCache }) return@let
            else return parameters.associate {
                it.uuid to paramsCache[it.uuid]!!
            }
        }

        val allParameters = node.parameters.parameters.associate { parameter ->
            parameter.uuid to when (parameter) {
                is NodeParameter.InputParameter, is NodeParameter.ControllableParameter.ControllableInputParameter -> {
                    connectionMap[parameter.uuid]?.let {
                        paramsCache[it.source.connectorUUID] ?: processParameterNode(
                            it.source.nodeUUID
                        )[it.source.connectorUUID]
                    } ?: if (parameter is NodeParameter.ControllableParameter.ControllableInputParameter)
                        parameter.value else 0.0f
                }

                is NodeParameter.ControllableParameter.InternalParameter -> parameter.value

                else -> 0.0f
            }
        }

        return (allParameters + node.parameters.computeOutputParams(allParameters)).also { paramsCache += it }
    }

    private fun processLaserNode(nodeUUID: NodeUUID) =
        nodes[nodeUUID]?.let { processLaserNode(it) } ?: emptyList()

    private fun processLaserNode(node: INodeBase): List<LaserObject> {
        laserOutputCache[node.uuid]?.let { return it }

        val output = when (node) {
            is OutputNode -> connectionMap[node.laserInputUUID]
                ?.let { processLaserNode(it.source.nodeUUID) } ?: emptyList()

            is InputNode -> throw Exception("Got into the InputNode branch without finding it in cache.")

            is GeneratorNode -> node.computeLaser(
                processParameterNode(node)
            )

            is TransformNode -> connectionMap[node.laserInputUUID]?.source?.nodeUUID
                ?.let {
                    node.transformLaser(
                        processLaserNode(it),
                        processParameterNode(node)
                    )
                } ?: emptyList()

            else -> emptyList()
        }

        laserOutputCache[node.uuid] = output
        return output
    }

    /**
     * Processes all nodes and returns the final output.
     *
     * @param macroValues Array of macro values to update the MacroNode parameters.
     * @return List of processed laser objects.
     */
    protected fun internalProcess(macroValues: FloatArray): List<LaserObject> {
        val outputNodes = nodes.values.filterIsInstance<OutputNode>().ifEmpty { return emptyList() }

        val laserObjects = mutableListOf<LaserObject>()

        // Setup cache for macros
        nodes.values.filterIsInstance<MacroNode>().forEach {
            paramsCache[it.macroOutputUUID] = macroValues[it.macroNumber]
        }

        for (outputNode in outputNodes) {
            val inputConnection = connectionMap[outputNode.laserInputUUID]
            val inputNode = inputConnection?.source?.nodeUUID?.let { nodes[it] }
            if (inputNode != null) {
                laserObjects.addAll(processLaserNode(inputNode))
            }
        }

        laserOutputCache.clear()
        paramsCache.clear()

        return laserObjects
    }

    open fun process(macroValues: FloatArray): List<LaserObject> {
        laserOutputCache.clear()
        paramsCache.clear()
        return internalProcess(macroValues)
    }
}


/**
 * Class representing a generator clip.
 */
@Serializable
class GeneratorClip(
    override var name: StringProperty = SimpleStringProperty("Unnamed Generator Clip"),
) : Clip()

/**
 * Class representing an effect clip.
 */
@Serializable
class EffectClip(
    override var name: StringProperty = SimpleStringProperty("Unnamed Generator Clip"),
) : Clip() {
    /**
     * Processes the effect clip with the provided input laser objects.
     *
     * @param input List of input laser objects.
     * @param macroValues Array of macro values to update the MacroNode parameters.
     * @return List of processed laser objects.
     */
    fun process(input: List<LaserObject>, macroValues: FloatArray): List<LaserObject> {
        laserOutputCache.clear()
        paramsCache.clear()
        // Prepare cache for input nodes
        nodes.values.filterIsInstance<InputNode>()
            .forEach { laserOutputCache[it.uuid] = input }
        return super.internalProcess(macroValues)
    }
}
