package ui.nodes.controls

import helpers.HBox
import helpers.StackPane
import helpers.bindTo
import helpers.setHgrow
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.HBox.setHgrow
import javafx.scene.layout.Priority
import javafx.scene.layout.Region

class SliderControl : NodeParameterControl() {
    private lateinit var valueLabel: Label
    private lateinit var slider: Slider
    override lateinit var control: Node

    override fun initControl(): Node {
        control = StackPane {
            children += HBox {
                alignment = Pos.CENTER_LEFT
                children += Label(parameter!!.name).apply {

                }
                children += Region().also {
                    setHgrow(it, Priority.ALWAYS)
                }
                children += Label().apply {
                    textProperty() bindTo value with parameter!!.valueConverter
                    alignment = Pos.CENTER_RIGHT
                }.also { valueLabel = it }

                isMouseTransparent = true
            }
            children += Slider().apply {
                valueProperty().bindBidirectional(super.value)
                min = parameter!!.range.start.toDouble()
                max = parameter!!.range.endInclusive.toDouble()

                addEventFilter(MouseEvent.ANY) { event ->
                    if (event.button == MouseButton.SECONDARY) {
                        if (event.eventType == MouseEvent.MOUSE_RELEASED) {
                            startValueEdit()
                        }
                        event.consume()
                    }
                }

                addEventFilter(MouseEvent.MOUSE_CLICKED) { event ->
                    if (event.isAltDown && event.button == MouseButton.PRIMARY) {
                        value = 0.0
                    }
                }
            }.also { slider = it }

            children[0].toFront()
        }
        return control
    }

    private fun startValueEdit() {
        val parent = (valueLabel.parent as HBox)
        val field = TextField(valueLabel.textProperty().get())
        parent.children[2] = field
        field.alignment = Pos.CENTER_RIGHT

        fun endEdit() {
            parent.children[2] = valueLabel
            field.text.toFloatOrNull()?.let { value.set(it) }
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
    }
}