package helpers.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import nodes.INodeBase
import nodes.implementations.generators.PointGeneratorNode
import nodes.implementations.special.InputNode
import nodes.implementations.special.MacroNode
import nodes.implementations.special.OutputNode
import nodes.implementations.transforms.HSVShiftNode
import nodes.implementations.transforms.PositionOffsetTransformNode

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

@OptIn(ExperimentalSerializationApi::class)
val json = Json {
    prettyPrint = true
    serializersModule = nodeSerializersModule
    namingStrategy = JsonNamingStrategy.SnakeCase
}