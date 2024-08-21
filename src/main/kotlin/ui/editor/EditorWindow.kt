package ui.editor

import clips.Clip
import clips.GeneratorClip
import helpers.ClipUUID
import helpers.serialization.json
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import kotlinx.serialization.encodeToString
import java.io.File
import kotlin.jvm.optionals.getOrNull

class EditorWindow private constructor(private val clip: Clip, private var initiallyEmpty: Boolean = false) {
    private val editorPane = EditorPane(clip)
    private lateinit var stage: Stage

    fun createAndShow() {
        Platform.runLater {
            stage = Stage().apply {
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

                setOnCloseRequest { event ->
                    event.consume()
                    this@EditorWindow.close()
                }

                show()

                if (initiallyEmpty)
                    editorPane.nodeCompositor.hasUnsavedChanges.set(true)
            }
        }
    }

    private fun close() {
        if (editorPane.nodeCompositor.hasUnsavedChanges.get()) {
            val alert = Alert(Alert.AlertType.CONFIRMATION).apply {
                title = "Unsaved Changes"
                headerText = "You have unsaved changes."
                contentText = "Do you want to save your changes before closing?"

                buttonTypes.setAll(ButtonType("Save"), ButtonType("Don't Save"), ButtonType.CANCEL)
            }

            val result = alert.showAndWait()

            when (result.getOrNull()?.text) {
                "Save" -> {
                    if (saveClip()) // make sure saving goes as planned
                        stage.close()
                }

                "Don't Save" -> {
                    stage.close()
                }

                else -> {
                    // Do nothing, just return to the editor
                }
            }
        } else {
            stage.close()
        }
    }

    private fun saveClip(): Boolean {
        if (initiallyEmpty) {
            initiallyEmpty = false
            return saveClipAs()
        }
        runCatching { File("${clip.name.value}.json").writeText(json.encodeToString<Clip>(clip)) }
            .onFailure {
                Alert(Alert.AlertType.ERROR, "Failed to save to file!", ButtonType.CLOSE)
                return false
            }
            .onSuccess { editorPane.nodeCompositor.hasUnsavedChanges.set(false) }
        return true
    }

    /**
     * @return true if a file was picked, false if not.
     */
    private fun saveClipAs() = FileChooser().apply {
        extensionFilters.add(FileChooser.ExtensionFilter("JSON Clip Files", "*.json"))
    }.showSaveDialog(stage)?.let { file ->
        clip.name.set(file.nameWithoutExtension)
        clip.uuid.set(ClipUUID())
        saveClip()
        true
    } ?: false

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
