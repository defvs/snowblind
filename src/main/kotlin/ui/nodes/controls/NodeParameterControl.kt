package ui.nodes.controls

import javafx.beans.property.FloatProperty
import javafx.beans.property.SimpleFloatProperty
import javafx.scene.Node
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

