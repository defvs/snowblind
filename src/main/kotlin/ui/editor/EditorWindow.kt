package ui.editor

import clips.Clip
import clips.GeneratorClip
import helpers.ClipUUID
import helpers.MenuItem
import helpers.serialization.json
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import kotlinx.serialization.encodeToString
import java.io.File

class EditorWindow private constructor(private val clip: Clip) {
    private val editorPane = EditorPane(clip)

    fun createAndShow() {
        Platform.runLater {
            val stage = Stage()

            val menuBar = MenuBar().apply {
                menus += Menu("File", null,
                    MenuItem("Save") { saveClip() },
                    MenuItem("Save As") { stage.saveClipAs() },
                    MenuItem("Cancel") { stage.closeWindow() }
                )
            }

            val root = BorderPane().apply {
                top = menuBar
                center = editorPane
            }
            stage.scene = Scene(root, 800.0, 600.0)
            stage.updateWindowTitle()
            stage.show()
        }
    }

    private fun saveClip() {
        File("${clip.name}.json").writeText(json.encodeToString<Clip>(clip))
    }

    private fun Stage.saveClipAs() {
        val fileChooser = FileChooser()
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("JSON Files", "*.json"))
        val selectedFile: File? = fileChooser.showSaveDialog(this)
        selectedFile?.let { file ->
            clip.name = file.nameWithoutExtension
            clip.uuid = ClipUUID()
            this.updateWindowTitle()
            saveClip()
        }
    }

    private fun Stage.closeWindow() {
        close()
    }

    private fun Stage.updateWindowTitle() {
        title = clip.name
    }

    companion object {
        fun openFromFile() {
            Platform.runLater {
                val fileChooser = FileChooser()
                fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("JSON Files", "*.json"))
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
            EditorWindow(GeneratorClip()).createAndShow()
        }
    }
}