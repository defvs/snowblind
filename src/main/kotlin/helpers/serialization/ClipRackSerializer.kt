package helpers.serialization

import clips.ClipRack
import helpers.ClipRackUUID
import helpers.ClipUUID
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

open class ClipRackSerializer(
    private val ignoreMissing: Boolean = false,
    private val extraPaths: Set<String> = emptySet(),
) : KSerializer<ClipRack> {
    @Serializable
    data class NameAndUUID(
        val name: String,
        val uuid: ClipUUID,
    )

    @Serializable
    private class ClipRackDelegate(
        val name: String,
        val uuid: ClipRackUUID,
        val generatorClips: Array<NameAndUUID?>,
        val effectClips: Array<NameAndUUID?>,
        val lookupPaths: Set<String>,
    ) {
        fun toClipRack(ignoreMissing: Boolean = false, extraPaths: Set<String>) = ClipRack.initWithLookup(
            name,
            uuid,
            generatorClips,
            effectClips,
            lookupPaths + extraPaths,
            ignoreMissing
        )

        constructor(clipRack: ClipRack) : this(
            clipRack.name.get(),
            clipRack.uuid.get(),
            clipRack.generatorClips.map {
                if (it == null) it
                else NameAndUUID(it.name.get(), it.uuid.value)
            }.toTypedArray(),
            clipRack.effectClips.map {
                if (it == null) it
                else NameAndUUID(it.name.get(), it.uuid.value)
            }.toTypedArray(),
            clipRack.lookupPaths
        )
    }

    private val delegateSerializer = ClipRackDelegate.serializer()
    override val descriptor = delegateSerializer.descriptor

    override fun deserialize(decoder: Decoder) =
        decoder.decodeSerializableValue(delegateSerializer).toClipRack(ignoreMissing, extraPaths)

    override fun serialize(encoder: Encoder, value: ClipRack) =
        encoder.encodeSerializableValue(delegateSerializer, ClipRackDelegate(value))

}

object DefaultClipRackSerializer : ClipRackSerializer()