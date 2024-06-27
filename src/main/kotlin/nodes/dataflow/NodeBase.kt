package nodes.dataflow

import java.util.UUID

typealias DataFlow = List<LaserObject>

abstract class NodeBase(
    val uuid: UUID,
    val name: String,
) {
    abstract val params: HashMap<ParameterType, NodeParameterData>

    fun updateParams(params: List<Parameter>) {
        params.forEach { this.params[it.first]?.data = it.second }
    }

    abstract fun process(inputs: List<DataFlow>): DataFlow
}
