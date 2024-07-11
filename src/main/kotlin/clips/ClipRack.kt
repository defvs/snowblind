package clips

import helpers.ClipUUID
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

lateinit var clipDatabase: Map<ClipUUID, Clip> // TODO: clipDatabase initialization in main program

@Serializable(with = ClipRackUUIDOnlySerializer::class)
class ClipRack(
    val name: String = "Unnamed Rack",
    val generatorClips: Array<GeneratorClip?> = Array(36) { null },
    val effectClips: Array<EffectClip?> = Array(12) { null }
) {

    fun generateOutput(enabledGeneratorClips: List<Int>, enabledEffectClips: List<Int>) =
        enabledGeneratorClips.flatMap { i ->
            generatorClips[i]?.process() ?: emptyList()
        }.let {
            enabledEffectClips.fold(it) { acc, i ->
                effectClips[i]?.process(acc) ?: acc
            }
        }
}

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

class ClipRackUUIDOnlySerializer : KSerializer<ClipRack> {
    private val delegateSerializer = ClipRackDelegate.serializer()
    override val descriptor = delegateSerializer.descriptor

    override fun deserialize(decoder: Decoder) =
        decoder.decodeSerializableValue(delegateSerializer).toClipRack(clipDatabase)

    override fun serialize(encoder: Encoder, value: ClipRack) =
        encoder.encodeSerializableValue(delegateSerializer, ClipRackDelegate(value))

}
