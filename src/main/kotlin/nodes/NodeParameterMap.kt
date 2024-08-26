package nodes

import helpers.ConnectorUUID
import helpers.replaceAllIndexed
import ui.nodes.controls.NodeParameterControl

class NodeParameterMap(
    vararg parameters: NodeParameter,
) {
    val parameters: List<NodeParameter> = parameters.toList()
    private val parametersByUUID: Map<ConnectorUUID, NodeParameter> = parameters.associateBy { it.uuid }
    private val parametersByName: Map<String, NodeParameter> = parameters.associateBy { it.name }

    operator fun get(name: String) = parametersByName[name]
    operator fun get(uuid: ConnectorUUID) = parametersByUUID[uuid]
    operator fun get(index: Int) = parameters[index]

    operator fun set(uuid: ConnectorUUID, value: Float) {
        (parametersByUUID[uuid] as? NodeParameter.ControllableParameter)?.value = value
    }

    operator fun set(index: Int, value: Float) {
        (parameters[index] as? NodeParameter.ControllableParameter)?.value = value
    }

    fun computeOutputParams(inputs: Map<ConnectorUUID, Float>) =
        parameters.filterIsInstance<NodeParameter.OutputParameter>().associate {
            it.uuid to it.compute(inputs)
        }

}

@DslMarker
annotation class NodeParameterDsl

@NodeParameterDsl
class NodeParameterMapBuilder {
    private val parameters = mutableListOf<NodeParameter>()

    fun internal(
        name: String,
        uuid: ConnectorUUID = ConnectorUUID(),
        range: ClosedFloatingPointRange<Float>,
        valueConverter: ValueConverter,
        control: NodeParameterControl,
        defaultValue: Float = 0f,
    ) = parameters.add(
        NodeParameter.ControllableParameter.InternalParameter(
            uuid, name, range, valueConverter, control, defaultValue
        ).also { control.parameter = it }
    )

    fun internalControllable(
        name: String,
        uuid: ConnectorUUID = ConnectorUUID(),
        range: ClosedFloatingPointRange<Float>,
        valueConverter: ValueConverter,
        control: NodeParameterControl,
        defaultValue: Float = 0f,
    ) = parameters.add(
        NodeParameter.ControllableParameter.ControllableInputParameter(
            uuid, name, range, valueConverter, control, defaultValue
        ).also { control.parameter = it }
    )

    fun output(
        name: String,
        uuid: ConnectorUUID = ConnectorUUID(),
        compute: ParameterCompute,
    ) = parameters.add(NodeParameter.OutputParameter(uuid, name, compute))

    fun input(
        name: String,
        uuid: ConnectorUUID = ConnectorUUID(),
    ) = parameters.add(NodeParameter.InputParameter(uuid, name))

    fun withExistingUUIDs(uuids: List<ConnectorUUID>) {
        this.parameters.replaceAllIndexed { index, source ->
            source.resetUUID(uuids[index])
        }
    }

    fun withExistingValues(values: Map<ConnectorUUID, Float>) {
        values.forEach { (uuid, value) ->
            when (val parameter = parameters.find { it.uuid == uuid }) {
                is NodeParameter.ControllableParameter -> {
                    parameter.control.value.set(value)
                }

                else -> throw Exception("withExistingValues called with an UUID of a non-controllable parameter.")
            }
        }
    }

    fun build(): NodeParameterMap = NodeParameterMap(*parameters.toTypedArray())
}

fun parameters(block: NodeParameterMapBuilder.() -> Unit): NodeParameterMap {
    val builder = NodeParameterMapBuilder()
    builder.block()
    return builder.build()
}
