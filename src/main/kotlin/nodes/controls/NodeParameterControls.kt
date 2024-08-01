package nodes.controls

import helpers.bindTo
import javafx.beans.property.FloatProperty
import javafx.beans.property.SimpleFloatProperty
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import nodes.NodeParameter

abstract class NodeParameterControl {
    var parameter: NodeParameter.ControllableParameter? = null
        set(value) {
            field = value
            if (value != null) this.value.set(value.defaultValue)
        }

    protected abstract val control: Node
    open var value: FloatProperty = SimpleFloatProperty()

    abstract fun initControl(): Node
}

class EmptyControl : NodeParameterControl() {
    override lateinit var control: Node

    override fun initControl(): Node {
        control = HBox()
        return control
    }
}

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
