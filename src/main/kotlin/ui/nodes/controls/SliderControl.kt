package ui.nodes.controls

import helpers.HBox
import helpers.Insets
import helpers.StackPane
import helpers.bindTo
import javafx.beans.binding.Bindings
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.control.TextField
import javafx.scene.control.skin.SliderSkin
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.HBox.setHgrow
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text

class SliderControl : NodeParameterControl() {
    class SliderControlSkin(slider: Slider) : SliderSkin(slider) {

        private val trackBackground: Rectangle = Rectangle()
        private val fill: Rectangle = Rectangle()

        init {
            trackBackground.fill = Color.web("000", 0.1)
            fill.fill = Color.web("000", 0.1)

            // Set rounded corners
            trackBackground.arcWidth = 10.0
            trackBackground.arcHeight = 10.0
            fill.arcWidth = 10.0
            fill.arcHeight = 10.0

            fill.isMouseTransparent = true

            children.addAll(trackBackground, fill)

            registerChangeListener(slider.valueProperty()) { updateFill() }
            registerChangeListener(slider.minProperty()) { updateFill() }
            registerChangeListener(slider.maxProperty()) { updateFill() }

            // Add mouse event handlers
            trackBackground.setOnMousePressed { event -> handleMouseEvent(event) }
            trackBackground.setOnMouseDragged { event -> handleMouseEvent(event) }

            // Bind the track height to the slider height
            trackBackground.heightProperty().bind(slider.heightProperty())
            fill.heightProperty().bind(slider.heightProperty())
        }

        override fun layoutChildren(x: Double, y: Double, w: Double, h: Double) {
            trackBackground.width = w
            trackBackground.relocate(x, y)
            updateFill()
        }

        private fun updateFill() {
            val slider = skinnable as Slider
            val range = slider.max - slider.min
            val proportion = (slider.value - slider.min) / range

            fill.width = trackBackground.width * proportion
            fill.relocate(trackBackground.layoutX, trackBackground.layoutY)
        }

        private fun handleMouseEvent(event: MouseEvent) {
            val slider = skinnable as Slider
            val mouseX = event.x
            val trackWidth = trackBackground.width
            val value = slider.min + (slider.max - slider.min) * (mouseX / trackWidth)
            slider.value = value.coerceIn(slider.min, slider.max)
        }
    }

    private lateinit var valueLabel: Label
    private lateinit var slider: Slider
    override lateinit var control: Node

    override fun initControl(): Node {
        control = StackPane {
            children += HBox {
                alignment = Pos.CENTER_LEFT
                children += Label(parameter!!.name).also {
                    minWidthProperty().bind(it.widthProperty().multiply(1.5))
                }
                children += Region().also {
                    setHgrow(it, Priority.ALWAYS)
                }
                children += Label().apply {
                    textProperty() bindTo value with parameter!!.valueConverter
                    alignment = Pos.CENTER_RIGHT
                }.also { valueLabel = it }

                isMouseTransparent = true

                padding = Insets(0.0, 8.0)
            }
            children += Slider().apply {
                skin = SliderControlSkin(this)

                valueProperty().bindBidirectional(super.value)
                min = parameter!!.range.start.toDouble()
                max = parameter!!.range.endInclusive.toDouble()

                addEventFilter(MouseEvent.ANY) { event ->
                    if (event.button == MouseButton.SECONDARY) {
                        if (event.eventType == MouseEvent.MOUSE_RELEASED) {
                            if (!isEditing) startValueEdit()
                        }
                        event.consume()
                    }
                    if (event.isAltDown && event.button == MouseButton.PRIMARY) {
                        if (event.eventType == MouseEvent.MOUSE_CLICKED)
                            value = 0.0
                        event.consume()
                    }
                }
            }.also { slider = it }

            children[0].toFront()
        }
        return control
    }

    private var isEditing = false

    private fun startValueEdit() {
        isEditing = true
        val parent = (valueLabel.parent as HBox)
        val field = TextField(valueLabel.textProperty().get())

        // Create a Text node to measure the width of the text
        val textNode = Text()
        textNode.textProperty().bind(field.textProperty())

        field.style = """
            -fx-background-color: transparent;
            -fx-border-color: transparent;
            -fx-padding: 0;
            -fx-text-fill: -fx-text-inner-color;
        """
        field.prefWidthProperty().bind(Bindings.createDoubleBinding({
            textNode.layoutBounds.width + 10 // Add some padding for better appearance
        }, textNode.layoutBoundsProperty()))

        parent.children[2] = field
        field.alignment = Pos.CENTER_RIGHT

        fun endEdit() {
            parent.children[2] = valueLabel
            parameter!!.valueConverter.fromString(field.text)?.let { value.set(it) }
            isEditing = false
        }

        field.focusedProperty().addListener { _, _, newValue ->
            if (newValue == false) endEdit()
        }
        field.setOnKeyPressed { event ->
            if (event.code == KeyCode.ENTER || event.code == KeyCode.ESCAPE) {
                endEdit()
            }
        }

        field.requestFocus()
        field.selectAll()  // Select all text when editing starts
    }
}
