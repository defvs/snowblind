package nodes

import helpers.ConnectorUUID
import nodes.controls.NodeParameterControl

class NodeParameterMap(
    vararg parameters: NodeParameterMapInput,
) {
    class NodeParameterMapInput(
        val definition: NodeParameterDefinition,
        val control: NodeParameterControl,
        val defaultValue: Float = 0f,
    )

    val definitions: List<NodeParameterDefinition>
    val parameters: List<NodeParameter>
    val controls: List<NodeParameterControl>
    private val parametersByUUID: Map<ConnectorUUID, NodeParameter>

    init {
        this.definitions = parameters.map { it.definition }
        this.parameters = parameters.map { NodeParameter(data = it.defaultValue) }
        this.controls = parameters.map { it.control }
        parametersByUUID = this.parameters.associateBy { it.uuid }
    }

    operator fun get(uuid: ConnectorUUID) = parametersByUUID[uuid]
    operator fun get(index: Int) = parameters[index]
    operator fun set(uuid: ConnectorUUID, value: Float) {
        parametersByUUID[uuid]!!.data = value
    }

    operator fun set(index: Int, value: Float) {
        parameters[index].data = value
    }
}

class NodeParameterMapBuilder {
    private val inputs = mutableListOf<NodeParameterMap.NodeParameterMapInput>()

    fun parameter(
        name: String,
        range: ClosedFloatingPointRange<Float>,
        valueConverter: ReadableValueConverter,
        control: NodeParameterControl,
        defaultValue: Float = 0f
    ) {
        val definition = NodeParameterDefinition(name, range, valueConverter)
        inputs.add(NodeParameterMap.NodeParameterMapInput(definition, control, defaultValue))
    }

    fun build(): NodeParameterMap = NodeParameterMap(*inputs.toTypedArray())
}

fun parameters(block: NodeParameterMapBuilder.() -> Unit): NodeParameterMap {
    val builder = NodeParameterMapBuilder()
    builder.block()
    return builder.build()
}
