package nodes

import helpers.ConnectorUUID
import ui.nodes.controls.NodeParameterControl

typealias ParameterCompute = (Map<ConnectorUUID, Float>) -> Float

sealed interface NodeParameter {
    val uuid: ConnectorUUID
    val name: String

    fun resetUUID(uuid: ConnectorUUID): NodeParameter

    sealed class ControllableParameter : NodeParameter {
        abstract val defaultValue: Float
        abstract val range: ClosedFloatingPointRange<Float>
        abstract val valueConverter: ValueConverter
        abstract val control: NodeParameterControl

        var value: Float
            get() = control.value.get()
            set(value) = control.value.set(value)

        fun initControl() = control.initControl()


        data class ControllableInputParameter(
            override val uuid: ConnectorUUID,
            override val name: String,
            override val range: ClosedFloatingPointRange<Float>,
            override val valueConverter: ValueConverter,
            override val control: NodeParameterControl,
            override val defaultValue: Float,
        ) : ControllableParameter() {
            override fun resetUUID(uuid: ConnectorUUID): NodeParameter = this.copy(uuid = uuid)
        }

        data class InternalParameter(
            override val uuid: ConnectorUUID,
            override val name: String,
            override val range: ClosedFloatingPointRange<Float>,
            override val valueConverter: ValueConverter,
            override val control: NodeParameterControl,
            override val defaultValue: Float,
        ) : ControllableParameter() {
            override fun resetUUID(uuid: ConnectorUUID): NodeParameter = this.copy(uuid = uuid)
        }
    }

    data class InputParameter(override val uuid: ConnectorUUID, override val name: String) : NodeParameter {
        override fun resetUUID(uuid: ConnectorUUID): NodeParameter = this.copy(uuid = uuid)
    }
    data class OutputParameter(
        override val uuid: ConnectorUUID,
        override val name: String,
        val compute: ParameterCompute,
    ) : NodeParameter {
        override fun resetUUID(uuid: ConnectorUUID): NodeParameter = this.copy(uuid = uuid)
    }
}

fun NodeParameterMap.mapToInput(
    inputParameters: Map<ConnectorUUID, Float>,
) = inputParameters.mapNotNull { (uuid, value) ->
    this[uuid]?.let { it.name to value }
}.toMap()