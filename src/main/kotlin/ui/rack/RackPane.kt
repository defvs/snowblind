package ui.rack

import clips.Clip
import clips.ClipRack
import helpers.StackPane
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.transformation.TransformationList
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.StackPane
import javafx.scene.layout.TilePane
import javafx.scene.layout.VBox

class RackPane(private val rack: ClipRack) : VBox() {
    val hasUnsavedChanges = SimpleBooleanProperty(false)

    private val generatorClipsPane = TilePane(Orientation.HORIZONTAL, 8.0, 8.0).apply {
        Bindings.bindContent(this.children, ClipToNodeObservableList(rack.generatorClips))
        prefColumns = 12
    }
    private val effectClipsPane = TilePane(Orientation.HORIZONTAL, 8.0, 8.0).apply {
        Bindings.bindContent(this.children, ClipToNodeObservableList(rack.effectClips))
        prefColumns = 12
    }

    init {
        children += generatorClipsPane
        children += Separator(Orientation.HORIZONTAL)
        children += effectClipsPane
        spacing = 8.0
        padding = Insets(8.0)
    }
}

private class ClipToNodeObservableList(source: ObservableList<out Clip?>) : TransformationList<Node, Clip>(source) {
    private fun createNodeForClip(clip: Clip?): StackPane {
        val labelTextProperty = clip?.name ?: SimpleStringProperty("empty")
        return StackPane(
            Label().apply { textProperty().bind(labelTextProperty) }
        ) {
            alignment = Pos.CENTER
            style = """
                -fx-background-color: white;
                -fx-border-color: black;
                -fx-border-width: 2;
                -fx-border-radius: 15;
                -fx-background-radius: 15;
            """
            prefWidth = 64.0
            prefHeight = 64.0
        }
    }

    override fun get(index: Int): Node {
        return createNodeForClip(source[index])
    }

    override fun getSourceIndex(index: Int): Int {
        return index
    }

    override fun getViewIndex(index: Int): Int {
        return index
    }

    override val size: Int
        get() = source.size

    override fun sourceChanged(c: ListChangeListener.Change<out Clip?>?) {}
}
