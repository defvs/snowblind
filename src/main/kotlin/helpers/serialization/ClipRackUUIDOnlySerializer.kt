package helpers.serialization

import clips.*
import helpers.ClipUUID
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class ClipRackUUIDOnlySerializer : KSerializer<ClipRack> {
    @Serializable
    private class ClipRackDelegate(
        val name: String,
        val generatorClipsUUID: Array<ClipUUID?>,
        val effectClipsUUID: Array<ClipUUID?>,
    ) {
        fun toClipRack(clipDatabase: Map<ClipUUID, Clip>) = ClipRack(
            name,
            generatorClipsUUID.map { clipDatabase[it] as? GeneratorClip }.toTypedArray(),
            effectClipsUUID.map { clipDatabase[it] as? EffectClip }.toTypedArray(),
        )

        constructor(clipRack: ClipRack) : this(
            clipRack.name,
            clipRack.generatorClips.map { it?.uuid }.toTypedArray(),
            clipRack.effectClips.map { it?.uuid }.toTypedArray()
        )
    }

    private val delegateSerializer = ClipRackDelegate.serializer()
    override val descriptor = delegateSerializer.descriptor

    override fun deserialize(decoder: Decoder) =
        decoder.decodeSerializableValue(delegateSerializer).toClipRack(ClipDatabase.database)

    override fun serialize(encoder: Encoder, value: ClipRack) =
        encoder.encodeSerializableValue(delegateSerializer, ClipRackDelegate(value))

}