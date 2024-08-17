package helpers.serialization.javafx

import javafx.beans.property.FloatProperty
import javafx.beans.property.SimpleFloatProperty
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object FloatPropertySerializer : KSerializer<FloatProperty> {
    private val delegateSerializer = Float.serializer()
    override val descriptor: SerialDescriptor = delegateSerializer.descriptor
    override fun deserialize(decoder: Decoder) = SimpleFloatProperty(delegateSerializer.deserialize(decoder))
    override fun serialize(encoder: Encoder, value: FloatProperty) = delegateSerializer.serialize(encoder, value.value)
}