package ui.nodes.controls

import javafx.scene.Node
import javafx.scene.layout.HBox

class EmptyControl : NodeParameterControl() {
    override lateinit var control: Node

    override fun initControl(): Node {
        control = HBox()
        return control
    }
}