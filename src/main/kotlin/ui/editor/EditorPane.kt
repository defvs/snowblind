package ui.editor

import clips.Clip
import javafx.scene.control.ScrollPane
import javafx.scene.control.SplitPane
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent


class EditorPane(clip: Clip) : SplitPane() {
    val nodeCompositor = NodeCompositorPane(clip)
    val nodeSelector = NodeSelectorPane(nodeCompositor)

    init {
        items.addAll(
            nodeSelector.apply {
                minWidth = 180.0
                maxWidth = 300.0
            },
            ScrollPane(nodeCompositor).apply {
                isPannable = true
                addEventHandler(MouseEvent.ANY) { event ->
                    if (event.button in listOf(MouseButton.SECONDARY, MouseButton.PRIMARY)) {
                        event.consume() // Prevent right & left-click from doing anything
                    }
                }

            }
        )

        setDividerPositions(0.3)
    }
}

