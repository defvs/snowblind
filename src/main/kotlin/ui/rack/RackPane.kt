package ui.rack

import ObservableMappedList
import clips.Clip
import clips.ClipRack
import clips.EffectClip
import clips.GeneratorClip
import helpers.MenuItem
import helpers.StackPane
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ListChangeListener
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.layout.TilePane
import javafx.scene.layout.VBox
import ui.editor.EditorWindow
import java.io.File

class RackPane(private val rack: ClipRack) : VBox() {
    val hasUnsavedChanges = SimpleBooleanProperty(false).apply {
        rack.name.addListener { _, _, _ -> this.set(true) }
        rack.uuid.addListener { _, _, _ -> this.set(true) }
        rack.generatorClips.addListener { _: ListChangeListener.Change<out Clip> -> this.set(true) }
        rack.effectClips.addListener { _: ListChangeListener.Change<out Clip> -> this.set(true) }
    }

    private val generatorClipsPane = TilePane(Orientation.HORIZONTAL, 8.0, 8.0).apply {
        Bindings.bindContent(this.children, ObservableMappedList(rack.generatorClips) { index, item ->
            createClipUIElement(
                index,
                item,
                "generatorClips"
            )
        })
        prefColumns = 12
    }
    private val effectClipsPane = TilePane(Orientation.HORIZONTAL, 8.0, 8.0).apply {
        Bindings.bindContent(this.children, ObservableMappedList(rack.effectClips) { index, item ->
            createClipUIElement(
                index,
                item,
                "effectClips"
            )
        })
        prefColumns = 12
    }

    init {
        children += generatorClipsPane
        children += Separator(Orientation.HORIZONTAL)
        children += effectClipsPane
        spacing = 8.0
        padding = Insets(8.0)

        setOnMouseClicked {
            if (it.target is StackPane && (it.target as? StackPane)?.styleClass?.contains("clipNode") == true) {
                onClipClicked(it)
                it.consume()
            }
        }
    }

    private fun onClipClicked(it: MouseEvent) = (it.target as? StackPane)?.let { target ->
        val clip = target.properties["clip"] as Clip?
        val index = target.properties["index"] as Int

        when (it.button) {
            MouseButton.SECONDARY -> {
                if (clip == null) {
                    fun addClip(clip: Clip?, path: File) {
                        if (target.styleClass.contains("generatorClips"))
                            rack.generatorClips[index] = clip as GeneratorClip
                        else
                            rack.effectClips[index] = clip as EffectClip
                        rack.lookupPaths.add(path.parent)
                    }
                    // Open menu to create or load clip
                    ContextMenu(
                        MenuItem("New") {
                            if (target.styleClass.contains("generatorClips"))
                                EditorWindow.createEmptyGeneratorClip(::addClip)
                            else
                                EditorWindow.createEmptyEffectClip(::addClip)
                        },
                        MenuItem("Open") {
                            EditorWindow.openFromFile(::addClip)
                        },
                        MenuItem("Load") {
                            EditorWindow.pickFile()?.let { (clip, _) ->
                                if (target.styleClass.contains("generatorClips"))
                                    rack.generatorClips[index] = clip as GeneratorClip
                                else
                                    rack.effectClips[index] = clip as EffectClip
                            }
                        }
                    ).show(target, Side.BOTTOM, 0.0, 0.0)
                } else {
                    // Open menu to edit, replace, or delete clip
                }
            }

            MouseButton.PRIMARY -> {
                // Enable clip output
            }

            else -> {}
        }
    }

    fun createClipUIElement(index: Int, clip: Clip?, type: String): StackPane {
        val labelTextProperty = clip?.name ?: SimpleStringProperty("empty")
        return StackPane(
            Label().apply {
                textProperty().bind(labelTextProperty)
                isMouseTransparent = true
            }
        ) {
            alignment = Pos.CENTER
            style = """
                -fx-background-color: white;
                -fx-border-color: black;
                -fx-border-width: 2;
                -fx-border-radius: 15;
                -fx-background-radius: 15;
            """
            prefWidth = 64.0
            prefHeight = 64.0

            styleClass += "clipNode"
            styleClass += type

            properties["index"] = index
            properties["clip"] = clip
        }
    }
}