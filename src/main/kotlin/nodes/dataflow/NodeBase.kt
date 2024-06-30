package nodes.dataflow

import java.util.UUID

typealias DataFlow = List<LaserObject>

abstract class NodeBase(
    val name: String,
    val description: String? = null,
) {
    val uuid: UUID = UUID.randomUUID()

    abstract val params: HashMap<ParameterType, NodeParameterData>
    val paramsInputCount
        get() = params.count { it.value.isExposed }

    fun updateParams(params: List<Parameter>) {
        params.forEach { this.params[it.first]?.data = it.second }
    }

    abstract fun process(inputs: List<DataFlow>): DataFlow
}
