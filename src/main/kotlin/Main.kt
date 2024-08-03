import clips.GeneratorClip
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import nodes.implementations.generators.PointGeneratorNode
import nodes.implementations.transforms.HSVShiftNode
import nodes.implementations.transforms.PositionOffsetTransformNode
import ui.editor.EditorPane

class ExampleApp : Application() {

    override fun start(primaryStage: Stage) {

        val draggablePane = EditorPane(GeneratorClip())
        listOf(
            HSVShiftNode(),
            PositionOffsetTransformNode(),
            PointGeneratorNode(),
        ).forEach(draggablePane.nodeCompositor::addNode)

        val scene = Scene(draggablePane, 800.0, 600.0)

        primaryStage.title = "Draggable Node Example"
        primaryStage.scene = scene
        primaryStage.show()
    }
}

fun main() {
    Application.launch(ExampleApp::class.java)
}
