package nodes

import laser.LaserObject
import java.util.*
import kotlin.collections.HashMap

abstract class NodeBase(
    val name: String,
    val description: String? = null,
) {
    val uuid: UUID = UUID.randomUUID()

    abstract val params: HashMap<ParameterType, NodeParameterData>
    val paramsInputCount get() = params.count { it.value.isExposed }

    fun updateParams(params: List<NodeParameter>) {
        params.forEach { this.params[it.first]?.data = it.second }
    }

    abstract fun process(inputs: List<List<LaserObject>>): List<LaserObject>
}

abstract class GeneratorNode(name: String, description: String? = null) : NodeBase(name, description)
abstract class TransformNode(name: String, description: String? = null) : NodeBase(name, description)