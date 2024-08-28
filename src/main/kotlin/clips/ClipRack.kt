package clips

import helpers.ClipRackUUID
import helpers.ClipUUID
import helpers.serialization.ClipRackSerializer
import helpers.serialization.DefaultClipRackSerializer
import helpers.serialization.json
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kotlinx.serialization.Serializable
import java.io.File

@Serializable(with = DefaultClipRackSerializer::class)
class ClipRack(
    val name: StringProperty = SimpleStringProperty("Unnamed Rack"),
    val uuid: ObjectProperty<ClipRackUUID> = SimpleObjectProperty(ClipRackUUID()),
    generatorClips: Array<GeneratorClip?> = arrayOfNulls(36),
    effectClips: Array<EffectClip?> = arrayOfNulls(12),
    lookupPaths: List<String> = emptyList(),
) {
    val generatorClips: ObservableList<GeneratorClip?>
    val effectClips: ObservableList<EffectClip?>
    val lookupPaths: ArrayList<String> = arrayListOf()

    init {
        require(generatorClips.size <= 36)
        require(effectClips.size <= 12)

        this.generatorClips = FXCollections.observableArrayList(*arrayOfNulls(36))
        generatorClips.forEachIndexed(this.generatorClips::set)

        this.effectClips = FXCollections.observableArrayList(*arrayOfNulls(12))
        effectClips.forEachIndexed(this.effectClips::set)

        this.lookupPaths.addAll(lookupPaths)
    }

    fun generateOutput(enabledGeneratorClips: List<Int>, enabledEffectClips: List<Int>) =
        enabledGeneratorClips.flatMap { i ->
            generatorClips[i]?.process(FloatArray(32) { 0f }) ?: emptyList() // fixme: macroArray
        }.let {
            enabledEffectClips.fold(it) { acc, i ->
                effectClips[i]?.process(acc, FloatArray(32) { 0f }) ?: acc
            }
        }

    companion object {
        fun initWithLookup(
            name: String,
            uuid: ClipRackUUID = ClipRackUUID(),
            generatorClips: Array<ClipRackSerializer.NameAndUUID?>,
            effectClips: Array<ClipRackSerializer.NameAndUUID?>,
            lookupPaths: List<String> = listOf("."),
            ignoreMissing: Boolean = false,
        ): ClipRack {
            require(generatorClips.size <= 36)
            require(effectClips.size <= 12)

            // Search for the clips in the paths
            val foundClips: Map<ClipUUID, Clip> = lookupPaths.flatMap { pathStr ->
                val dir = File(pathStr)
                if (!dir.isDirectory) return@flatMap listOf()
                dir.listFiles { _: File, name: String ->
                    name.endsWith(".sbc")
                }?.mapNotNull {
                    runCatching { json.decodeFromString<Clip>(it.readText()) }
                        .onFailure { /* TODO: LOGGING */ }
                        .getOrNull()
                } ?: listOf()
            }.associateBy { it.uuid.get() }

            val exceptions = arrayListOf<ClipRackSerializer.NameAndUUID>()

            val generatorClipsMatched = buildList {
                generatorClips.forEach { value ->
                    if (value == null) add(null)
                    else (foundClips[value.uuid] as? GeneratorClip).let {
                        if (it == null) exceptions.add(value)
                        this.add(it)
                    }
                }
            }.toTypedArray()

            val effectClipsMatched = buildList {
                effectClips.forEach { value ->
                    if (value == null) add(null)
                    else (foundClips[value.uuid] as? EffectClip).let {
                        if (it == null) exceptions.add(value)
                        this.add(it)
                    }
                }
            }.toTypedArray()

            if (!ignoreMissing && exceptions.isNotEmpty()) throw ClipsNotFoundException(exceptions, lookupPaths)

            return ClipRack(
                SimpleStringProperty(name),
                SimpleObjectProperty(uuid),
                generatorClipsMatched,
                effectClipsMatched,
                lookupPaths,
            )
        }
    }
}

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter", "unused", "RedundantSuppression")
class ClipsNotFoundException(val clips: List<ClipRackSerializer.NameAndUUID>, val lookupLocations: List<String>) :
    Exception("Some Clips not found in search paths.")