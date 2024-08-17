package helpers

import javafx.beans.property.FloatProperty
import javafx.beans.property.SimpleFloatProperty
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

typealias ObservablePosition = @Serializable(with = ObservablePositionSerializer::class) Pair<FloatProperty, FloatProperty>

fun ObservablePosition() = Pair(SimpleFloatProperty(0f), SimpleFloatProperty(0f))

object ObservablePositionSerializer : KSerializer<ObservablePosition> {
    @Serializable
    @SerialName("Position")
    private data class Surrogate(val x: Float, val y: Float)

    private val surrogateSerializer = Surrogate.serializer()
    override val descriptor = surrogateSerializer.descriptor

    override fun deserialize(decoder: Decoder) =
        decoder.decodeSerializableValue(surrogateSerializer).let {
            ObservablePosition(SimpleFloatProperty(it.x), SimpleFloatProperty(it.y))
        }

    override fun serialize(encoder: Encoder, value: ObservablePosition) {
        encoder.encodeSerializableValue(
            surrogateSerializer,
            Surrogate(value.first.value, value.second.value)
        )
    }

}