package clips

class ClipRack(val name: String = "Unnamed Rack") {
    val generatorClips = Array<GeneratorClip?>(36) { null }
    val effectClips = Array<EffectClip?>(12) { null }

    fun generateOutput(enabledGeneratorClips: List<Int>, enabledEffectClips: List<Int>) =
        enabledGeneratorClips.flatMap { i ->
            generatorClips[i]?.process() ?: emptyList()
        }.let {
            enabledEffectClips.fold(it) { acc, i ->
                effectClips[i]?.process(acc) ?: acc
            }
        }
}