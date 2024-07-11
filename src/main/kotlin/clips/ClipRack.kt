package clips

import helpers.ClipRackUUID
import helpers.serialization.ClipRackUUIDOnlySerializer
import kotlinx.serialization.Serializable

@Serializable(with = ClipRackUUIDOnlySerializer::class)
class ClipRack(
    val name: String = "Unnamed Rack",
    val uuid: ClipRackUUID = ClipRackUUID(),
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
