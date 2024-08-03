package ui.editor

import clips.Clip
import javafx.scene.layout.BorderPane
import ui.nodes.NodeCompositorPane

class EditorPane(clip: Clip) : BorderPane() {
    val nodeCompositor: NodeCompositorPane = NodeCompositorPane(clip)
    val nodeSelector = NodeSelectorPane(nodeCompositor)

    init {
        left = nodeSelector
        center = nodeCompositor
    }
}