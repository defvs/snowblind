package nodes

import helpers.ConnectorUUID
import helpers.zipTriple
import ui.nodes.controls.NodeParameterControl

class NodeParameterMap(
    vararg parameters: NodeParameterMapInput,
) {
    class NodeParameterMapInput(
        val definition: NodeParameterDefinition,
        val control: NodeParameterControl,
        val defaultValue: Float = 0f,
    )

    internal val definitions: List<NodeParameterDefinition>
    internal val parameters: List<NodeParameter>
    internal val controls: List<NodeParameterControl>
    internal val parametersByUUID: Map<ConnectorUUID, NodeParameter>

    init {
        this.definitions = parameters.map { it.definition }
        this.parameters = parameters.map { NodeParameter(data = it.defaultValue) }
        this.controls = parameters.map { it.control }
        this.parametersByUUID = this.parameters.associateBy { it.uuid }
    }

    // region Getters and Setters
    fun getDefinition(index: Int) = definitions[index]
    fun getParameter(index: Int) = parameters[index]
    fun getParameter(uuid: ConnectorUUID) = parametersByUUID[uuid]
    fun getControl(index: Int) = controls[index]
    fun getValue(index: Int) = getParameter(index).data
    fun getValue(uuid: ConnectorUUID) = getParameter(uuid)?.data
    fun setValue(index: Int, value: Float) { getParameter(index).data = value }
    fun setValue(uuid: ConnectorUUID, value: Float) { getParameter(uuid)!!.data = value }
    fun getAll(index: Int) = Triple(getDefinition(index), getParameter(index), getControl(index))
    // endregion

    // region Helpers
    fun flatten() = definitions.zipTriple(parameters, controls)

    fun initAllControls() = definitions.zipTriple(parameters, controls).map { (def, param, control) ->
        control.createControl(param.data, def)
    }
    // endregion

    // region Operators
    operator fun get(index: Int) = getValue(index)
    operator fun set(index: Int, value: Float) = setValue(index, value)
    // endregion

}

class NodeParameterMapBuilder {
    private val inputs = mutableListOf<NodeParameterMap.NodeParameterMapInput>()

    fun parameter(
        name: String,
        range: ClosedFloatingPointRange<Float>,
        valueConverter: ReadableValueConverter,
        control: NodeParameterControl,
        defaultValue: Float = 0f,
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
