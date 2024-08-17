package helpers.serialization.javafx

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object StringPropertySerializer : KSerializer<StringProperty> {
    private val delegateSerializer = String.serializer()
    override val descriptor: SerialDescriptor = delegateSerializer.descriptor
    override fun deserialize(decoder: Decoder) = SimpleStringProperty(delegateSerializer.deserialize(decoder))
    override fun serialize(encoder: Encoder, value: StringProperty) = delegateSerializer.serialize(encoder, value.value)
}