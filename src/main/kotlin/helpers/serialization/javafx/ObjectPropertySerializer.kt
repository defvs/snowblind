package helpers.serialization.javafx

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class ObjectPropertySerializer<T>(private val delegateSerializer: KSerializer<T>): KSerializer<ObjectProperty<T>> {
    override val descriptor: SerialDescriptor get() = delegateSerializer.descriptor
    override fun deserialize(decoder: Decoder) = SimpleObjectProperty(delegateSerializer.deserialize(decoder))
    override fun serialize(encoder: Encoder, value: ObjectProperty<T>) = delegateSerializer.serialize(encoder, value.value)
}