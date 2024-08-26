import clips.GeneratorClip
import helpers.HBox
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.stage.Stage
import nodes.implementations.generators.PointGeneratorNode
import nodes.implementations.transforms.HSVShiftNode
import nodes.implementations.transforms.PositionOffsetTransformNode
import ui.editor.EditorWindow

class ExampleApp : Application() {

    override fun start(primaryStage: Stage) {
        val scene = Scene(HBox(
            Button("Open from file").apply {
                setOnMouseClicked { EditorWindow.openFromFile() }
            },
            Button("New (empty Generator)").apply {
                setOnMouseClicked { EditorWindow.createEmptyGeneratorClip() }
            },
            Button("New (empty Effect)").apply {
                setOnMouseClicked { EditorWindow.createEmptyEffectClip() }
            },
            Button("New (test not empty)").apply {
                setOnMouseClicked {
                    EditorWindow.open(GeneratorClip().apply {
                        this += HSVShiftNode()
                        this += PositionOffsetTransformNode()
                        this += PointGeneratorNode()
                    })
                }
            }
        ), 800.0, 600.0)

        primaryStage.title = "Snowblind (testing)"
        primaryStage.scene = scene
        primaryStage.show()
    }
}

fun main() {
    Application.launch(ExampleApp::class.java)
}
