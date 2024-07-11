package helpers

import nodes.implementations.generators.*
import nodes.implementations.special.*
import nodes.implementations.transforms.*
import kotlin.reflect.full.hasAnnotation

@Suppress("MemberVisibilityCanBePrivate", "unused")
object AllNodes {
    val GENERATOR_NODES = arrayOf(
        PointGeneratorNode::class,
    )

    val SPECIAL_NODES = arrayOf(
        InputNode::class,
        MacroNode::class,
        OutputNode::class,
    )

    val TRANSFORM_NODES = arrayOf(
        HSVShiftNode::class,
        PositionOffsetTransformNode::class,
    )

    val ALL_NODES
        get() = listOf(*GENERATOR_NODES, *SPECIAL_NODES, *TRANSFORM_NODES)

    val FULLCLIP_AVAILABLE_NODES
        get() = ALL_NODES.filter { it.hasAnnotation<OnlyFullClipNode>() }

    val FXCLIP_AVAILABLE_NODES
        get() = ALL_NODES.filter { it.hasAnnotation<OnlyFXClipNode>() }

}