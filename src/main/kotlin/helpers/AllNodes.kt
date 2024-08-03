package helpers

import nodes.INodeBase
import nodes.implementations.generators.*
import nodes.implementations.special.*
import nodes.implementations.transforms.*
import kotlin.reflect.KClass
import kotlin.reflect.full.hasAnnotation

@Suppress("MemberVisibilityCanBePrivate", "unused")
object AllNodes {
    val GENERATOR_NODES: List<KClass<out INodeBase>> = listOf(
        PointGeneratorNode::class,
    )

    val SPECIAL_NODES: List<KClass<out INodeBase>> = listOf(
        InputNode::class,
        MacroNode::class,
        OutputNode::class,
    )

    val TRANSFORM_NODES: List<KClass<out INodeBase>> = listOf(
        HSVShiftNode::class,
        PositionOffsetTransformNode::class,
    )

    val ALL_NODES by lazy {
        buildList {
            addAll(GENERATOR_NODES)
            addAll(SPECIAL_NODES)
            addAll(TRANSFORM_NODES)
        }
    }

    val FULLCLIP_AVAILABLE_NODES
        get() = ALL_NODES.filter { it.hasAnnotation<OnlyFullClipNode>() }

    val FXCLIP_AVAILABLE_NODES
        get() = ALL_NODES.filter { it.hasAnnotation<OnlyFXClipNode>() }

}