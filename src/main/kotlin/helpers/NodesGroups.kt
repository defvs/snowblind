package helpers

import nodes.INodeBase
import kotlin.reflect.KClass

class NodesGroupsList internal constructor(private val groups: Map<NodesGroups, List<KClass<out INodeBase>>>) :
    Map<NodesGroupsList.NodesGroups, List<KClass<out INodeBase>>> by groups {
    enum class NodesGroups(val categoryTitle: String) {
        Generators("Laser Generators"),
        Transforms("Laser Transforms"),
        ParameterTransforms("Parameter Transforms"),
        Specials("Special nodes"),
    }

    operator fun plus(other: NodesGroupsList) = NodesGroupsList(zipMapOfList(this, other))
}

@DslMarker
annotation class NodeClassesDsl

@NodeClassesDsl
class NodesGroupsListBuilder {
    private val groups: MutableMap<NodesGroupsList.NodesGroups, MutableList<KClass<out INodeBase>>> = mutableMapOf()

    fun generators(vararg classes: KClass<out INodeBase>) {
        groups.getOrPut(NodesGroupsList.NodesGroups.Generators) { mutableListOf() }.addAll(classes)
    }

    fun transforms(vararg classes: KClass<out INodeBase>) {
        groups.getOrPut(NodesGroupsList.NodesGroups.Transforms) { mutableListOf() }.addAll(classes)
    }

    fun parameterTransforms(vararg classes: KClass<out INodeBase>) {
        groups.getOrPut(NodesGroupsList.NodesGroups.ParameterTransforms) { mutableListOf() }.addAll(classes)
    }

    fun specials(vararg classes: KClass<out INodeBase>) {
        groups.getOrPut(NodesGroupsList.NodesGroups.Specials) { mutableListOf() }.addAll(classes)
    }

    fun build(): NodesGroupsList = NodesGroupsList(groups)
}

fun nodeClasses(initializer: NodesGroupsListBuilder.() -> Unit): NodesGroupsList {
    val builder = NodesGroupsListBuilder()
    builder.initializer()
    return builder.build()
}