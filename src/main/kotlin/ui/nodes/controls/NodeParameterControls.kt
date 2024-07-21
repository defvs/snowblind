package ui.nodes.controls

import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import nodes.NodeParameterDefinition
import nodes.ReadableValueConverter

abstract class NodeParameterControl {
    protected var control: Node? = null
    open var value: Float = 0.0f

    protected abstract fun initControl(
        value: Float,
        range: ClosedFloatingPointRange<Float>,
        valueConverter: ReadableValueConverter,
    )

    fun createControl(
        value: Float,
        range: ClosedFloatingPointRange<Float>,
        valueConverter: ReadableValueConverter,
    ) = control ?: initControl(value, range, valueConverter).let { control!! }

    fun createControl(value: Float, definition: NodeParameterDefinition): Node = createControl(
        value,
        definition.range,
        definition.valueConverter,
    )
}

class EmptyControl : NodeParameterControl() {
    override fun initControl(
        value: Float,
        range: ClosedFloatingPointRange<Float>,
        valueConverter: ReadableValueConverter
    ) {
        control = HBox()
    }
}

class TestControl : NodeParameterControl() {
    private lateinit var valueLabel: Label

    private var actualValue: Float = 0.0f
    private lateinit var valueConverter: ReadableValueConverter

    override var value: Float
        get() = actualValue
        set(value) {
            actualValue = value
            valueLabel.text = valueConverter(value)
        }

    override fun initControl(
        value: Float,
        range: ClosedFloatingPointRange<Float>,
        valueConverter: ReadableValueConverter,
    ) {
        control = HBox(
            Label("Test"),
            Label("").also { valueLabel = it },
        )
        this.value = value
        this.valueConverter = valueConverter
    }

}