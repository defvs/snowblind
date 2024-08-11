package ui.editor

import clips.Clip
import javafx.scene.control.SplitPane

class EditorPane(clip: Clip) : SplitPane() {
    private val nodeCompositor = NodeCompositorPane(clip)
    private val nodeSelector = NodeSelectorPane(nodeCompositor)

    init {
        items.addAll(
            nodeSelector.apply {
                minWidth = 180.0
                maxWidth = 300.0
            },
            nodeCompositor
        )

        setDividerPositions(0.3)
    }
}

