package ui.rack

import clips.Clip
import clips.ClipRack
import clips.ClipsNotFoundException
import helpers.ClipRackUUID
import helpers.serialization.ClipRackSerializer
import helpers.serialization.json
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import kotlinx.serialization.encodeToString
import java.io.File
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull

class RackWindow private constructor(private val rack: ClipRack, private var savePath: String? = null) {
    private lateinit var stage: Stage
    private val editorPane = RackPane(rack)

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

                scene = Scene(root)
                isResizable = false

                titleProperty().bind(Bindings.createStringBinding({
                    if (editorPane.hasUnsavedChanges.get()) "• ${rack.name.value}" else rack.name.value
                }, rack.name, editorPane.hasUnsavedChanges))

                setOnCloseRequest { event ->
                    event.consume()
                    this@RackWindow.close()
                }

                show()

                if (savePath == null)
                    editorPane.hasUnsavedChanges.set(true)
            }
        }
    }

    private fun close() {
        if (editorPane.hasUnsavedChanges.get()) {
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
        if (savePath == null) {
            return saveClipAs()
        }
        runCatching { File(savePath!!).writeText(json.encodeToString<ClipRack>(rack)) }
            .onFailure {
                Alert(Alert.AlertType.ERROR, "Failed to save to file!", ButtonType.CLOSE)
                return false
            }
            .onSuccess { editorPane.hasUnsavedChanges.set(false) }
        return true
    }

    /**
     * @return true if a file was picked, false if not.
     */
    private fun saveClipAs() = FileChooser().apply {
        extensionFilters.add(FileChooser.ExtensionFilter("Rack Files (.sbr)", "*.sbr"))
    }.showSaveDialog(stage)?.let { file ->
        savePath = file.absolutePath.let { if (it.endsWith(".sbr")) it else "$it.sbr" }
        rack.name.set(file.nameWithoutExtension)
        rack.uuid.set(ClipRackUUID())
        saveClip()
        true
    } ?: false

    companion object {
        fun openFromFile() {
            Platform.runLater {
                val fileChooser = FileChooser()
                fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("Rack Files (.sbr)", "*.sbr"))
                fileChooser.showOpenDialog(null)?.let { loadFromFile(it) }
            }
        }

        private fun loadFromFile(file: File) {
            val deserializer = ClipRackSerializer()
            runCatching { json.decodeFromString(deserializer, file.readText()) }
                .recoverCatching {
                    askForMissing(file, it as ClipsNotFoundException)
                }
                .onSuccess { rack ->
                    RackWindow(
                        rack,
                        file.absolutePath.let { if (it.endsWith(".sbr")) it else "$it.sbr" }
                    ).createAndShow()
                }
        }

        private fun askForMissing(file: File, exception: ClipsNotFoundException) =
            when (val result = MissingClipsDialog(exception).showDialog()) {
                MissingClipsDialog.MissingClipsDialogResult.Cancel -> {
                    throw exception
                }

                MissingClipsDialog.MissingClipsDialogResult.Ignore -> {
                    json.decodeFromString(
                        ClipRackSerializer(ignoreMissing = true),
                        file.readText()
                    )
                }

                is MissingClipsDialog.MissingClipsDialogResult.Reload -> {
                    json.decodeFromString(
                        ClipRackSerializer(ignoreMissing = false, extraPaths = result.newPaths),
                        file.readText()
                    )
                }
            }

        fun open(rack: ClipRack) {
            RackWindow(rack).createAndShow()
        }

        fun createEmpty() = open(ClipRack())
    }
}

class MissingClipsDialog(exception: ClipsNotFoundException) :
    Dialog<MissingClipsDialog.MissingClipsDialogResult>() {
    sealed interface MissingClipsDialogResult {
        data class Reload(val newPaths: Set<String>) : MissingClipsDialogResult
        data object Ignore : MissingClipsDialogResult
        data object Cancel : MissingClipsDialogResult
    }

    init {
        val missingClips = exception.clips
        val lookupPaths = hashSetOf<String>()

        // Data model for table
        data class ClipEntry(val name: String, val pathProperty: StringProperty)

        val clipEntries = missingClips.map { ClipEntry(it.name, SimpleStringProperty("")) }.toMutableList()

        // TableView setup
        val tableView = TableView<ClipEntry>().apply {
            items = FXCollections.observableArrayList(clipEntries)

            // First column for clip names
            columns.add(TableColumn<ClipEntry, String>("Clip Name").apply {
                cellValueFactory = PropertyValueFactory("name")
                isEditable = false
            })

            // Second column for file paths
            columns.add(TableColumn<ClipEntry, String>("File Path").apply {
                cellValueFactory = PropertyValueFactory("pathProperty")
                setCellFactory {
                    object : TableCell<ClipEntry, String>() {
                        private val button = Button("Select File").apply {
                            setOnAction {
                                val fileChooser = FileChooser().apply {
                                    extensionFilters.add(FileChooser.ExtensionFilter("Clip Files", "*.sbc"))
                                }
                                val selectedFile = fileChooser.showOpenDialog(scene.window)
                                selectedFile?.let { file ->
                                    val clip = runCatching { json.decodeFromString<Clip>(file.readText()) }
                                        .getOrNull()
                                    val entry = tableView.items[index]
                                    if (clip != null && clip.uuid.get() == missingClips[index].uuid) {
                                        entry.pathProperty.set(file.absolutePath)
                                        lookupPaths.add(file.parent)
                                    } else {
                                        Alert(
                                            Alert.AlertType.ERROR,
                                            "UUID mismatch or invalid clip file!",
                                            ButtonType.OK
                                        ).showAndWait()
                                    }
                                }
                            }
                        }

                        override fun updateItem(item: String?, empty: Boolean) {
                            super.updateItem(item, empty)
                            graphic = if (empty) null else button
                        }
                    }
                }
            })
        }

        val reloadButton: ButtonType
        val ignoreButton: ButtonType

        // Dialog layout
        val dialogPane = DialogPane().apply {
            content = tableView
            buttonTypes.addAll(
                ButtonType("Reload", ButtonBar.ButtonData.OK_DONE).also { reloadButton = it },
                ButtonType("Ignore").also { ignoreButton = it },
                ButtonType.CANCEL
            )
            lookupButton(ButtonType("Reload")).disableProperty().bind(Bindings.createBooleanBinding({
                clipEntries.all { it.pathProperty.isNotEmpty.value }
            }, *clipEntries.map { it.pathProperty.isNotEmpty }.toTypedArray()))
        }


        title = "Clips Not Found"
        headerText = "The following clips were not found. Please provide their locations."
        this.dialogPane = dialogPane

        setResultConverter { button ->
            when (button) {
                ignoreButton -> MissingClipsDialogResult.Ignore
                reloadButton -> MissingClipsDialogResult.Reload(lookupPaths)
                else -> MissingClipsDialogResult.Cancel
            }
        }
    }

    fun showDialog(): MissingClipsDialogResult = showAndWait().getOrElse { MissingClipsDialogResult.Cancel }
}