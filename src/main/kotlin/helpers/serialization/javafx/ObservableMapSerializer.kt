package helpers.serialization.javafx

import javafx.collections.FXCollections
import javafx.collections.ObservableMap
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class ObservableMapSerializer<K, V>(
    keySerializer: KSerializer<K>,
    valueSerializer: KSerializer<V>,
) : KSerializer<ObservableMap<K, V>> {
    private val delegateSerializer = MapSerializer(keySerializer, valueSerializer)
    override val descriptor = delegateSerializer.descriptor
    override fun deserialize(decoder: Decoder) =
        FXCollections.observableHashMap<K, V>().apply { this.putAll(delegateSerializer.deserialize(decoder)) }!!

    override fun serialize(encoder: Encoder, value: ObservableMap<K, V>) {
        delegateSerializer.serialize(encoder, value)
    }
}