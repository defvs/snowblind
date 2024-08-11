package ui.nodes.controls

import helpers.bindTo
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox

class TestControl : NodeParameterControl() {
    private lateinit var valueLabel: Label
    override lateinit var control: Node

    override fun initControl(): Node {
        control = HBox(
            Label(parameter!!.name),
            Label("").apply {
                textProperty() bindTo value with parameter!!.valueConverter
            }.also { valueLabel = it },
        )
        return control
    }
}