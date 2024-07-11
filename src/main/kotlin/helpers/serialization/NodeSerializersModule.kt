package helpers.serialization

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import nodes.INodeBase
import nodes.implementations.special.*
import nodes.implementations.generators.*
import nodes.implementations.transforms.*

val nodeSerializersModule = SerializersModule {
    polymorphic(INodeBase::class) {
        subclass(PointGeneratorNode::class)
        subclass(InputNode::class)
        subclass(MacroNode::class)
        subclass(OutputNode::class)
        subclass(HSVShiftNode::class)
        subclass(PositionOffsetTransformNode::class)
    }
}