package helpers

import javafx.beans.binding.Bindings
import javafx.beans.property.FloatProperty
import javafx.beans.property.StringProperty
import nodes.helpers.ReadableValueConverter

fun <T> MutableList<T>.replaceAllIndexed(transform: (index: Int, source: T) -> T) {
    for (i in this.indices) {
        this[i] = transform(i, this[i])
    }
}

class LabelTextPropertyBinder(private val stringProperty: StringProperty, private val value: FloatProperty) {
    infix fun with(valueConverter: ReadableValueConverter) {
        stringProperty.bind(
            Bindings.createStringBinding(
                { valueConverter(value.get()) }, value
            )
        )

    }
}

infix fun StringProperty.bindTo(value: FloatProperty) = LabelTextPropertyBinder(this, value)