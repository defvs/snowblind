package ui.editor

import clips.Clip
import clips.GeneratorClip
import helpers.ClipUUID
import helpers.serialization.json
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import kotlinx.serialization.encodeToString
import java.io.File

class EditorWindow private constructor(private val clip: Clip, private var initiallyEmpty: Boolean = false) {
    private val editorPane = EditorPane(clip)
    private lateinit var stage: Stage

    fun createAndShow() {
        Platform.runLater {
            stage = Stage()
            with(stage) {
                val menuBar = MenuBar().apply {
                    menus += Menu("File", null,
                        MenuItem("Save").apply {
                            accelerator = KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN)
                            setOnAction { saveClip() }
                        },
                        MenuItem("Save As").apply {
                            accelerator =
                                KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN)
                            setOnAction { saveClipAs() }
                        },
                        MenuItem("Close").apply {
                            accelerator = KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN)
                            setOnAction { close() }
                        }
                    )
                }

                val root = BorderPane().apply {
                    top = menuBar
                    center = editorPane
                }

                scene = Scene(root, 800.0, 600.0)

                titleProperty().bind(Bindings.createStringBinding({
                    if (editorPane.nodeCompositor.hasUnsavedChanges.get()) "• ${clip.name.value}" else clip.name.value
                }, clip.name, editorPane.nodeCompositor.hasUnsavedChanges))

                show()

                if (initiallyEmpty)
                    editorPane.nodeCompositor.hasUnsavedChanges.set(true)
            }
        }
    }

    private fun saveClip() {
        if (initiallyEmpty) {
            initiallyEmpty = false
            return saveClipAs()
        }
        File("${clip.name.value}.json").writeText(json.encodeToString<Clip>(clip))
        editorPane.nodeCompositor.hasUnsavedChanges.set(false)
    }

    private fun saveClipAs() {
        val fileChooser = FileChooser()
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("JSON Clip Files", "*.json"))
        val selectedFile: File? = fileChooser.showSaveDialog(stage)
        selectedFile?.let { file ->
            clip.name.set(file.nameWithoutExtension)
            clip.uuid.set(ClipUUID())
            saveClip()
        }
    }

    companion object {
        fun openFromFile() {
            Platform.runLater {
                val fileChooser = FileChooser()
                fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("JSON Clip Files", "*.json"))
                val selectedFile: File? = fileChooser.showOpenDialog(null)
                selectedFile?.let { file ->
                    val clip = json.decodeFromString<Clip>(file.readText())
                    EditorWindow(clip).createAndShow()
                }
            }
        }

        fun open(clip: Clip) {
            EditorWindow(clip).createAndShow()
        }

        fun createEmpty() {
            EditorWindow(GeneratorClip(), initiallyEmpty = true).createAndShow()
        }
    }
}