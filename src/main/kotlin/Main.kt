import helpers.HBox
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.stage.Stage
import ui.editor.EditorWindow
import ui.rack.RackWindow

class ExampleApp : Application() {

    override fun start(primaryStage: Stage) {
        val scene = Scene(
            VBox(
                Label("Node editor"),
                HBox(
                    Button("Open from file").apply {
                        setOnMouseClicked { EditorWindow.openFromFile() }
                    },
                    Button("New (empty Generator)").apply {
                        setOnMouseClicked { EditorWindow.createEmptyGeneratorClip() }
                    },
                    Button("New (empty Effect)").apply {
                        setOnMouseClicked { EditorWindow.createEmptyEffectClip() }
                    },
                ),
                Label("Rack"),
                HBox(
                    Button("Open from file").apply {
                        setOnMouseClicked { RackWindow.openFromFile() }
                    },
                    Button("New (empty)").apply {
                        setOnMouseClicked { RackWindow.createEmpty() }
                    },
                ),
            ), 800.0, 600.0
        )

        primaryStage.title = "Snowblind (testing)"
        primaryStage.scene = scene
        primaryStage.show()
    }
}

fun main() {
    Application.launch(ExampleApp::class.java)
}
