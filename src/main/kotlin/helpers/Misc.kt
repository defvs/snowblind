package helpers

import helpers.serialization.javafx.FloatPropertySerializer
import javafx.beans.binding.Bindings
import javafx.beans.property.FloatProperty
import javafx.beans.property.SimpleFloatProperty
import javafx.beans.property.StringProperty
import kotlinx.serialization.Serializable
import nodes.helpers.ValueConverter

fun <T, U, V> Iterable<T>.zipTriple(other1: Iterable<U>, other2: Iterable<V>): ArrayList<Triple<T, U, V>> {
    val first = iterator()
    val second = other1.iterator()
    val third = other2.iterator()
    val list = ArrayList<Triple<T, U, V>>(
        minOf(
            if (first is Collection<*>) first.size else 10,
            if (second is Collection<*>) second.size else 10,
            if (third is Collection<*>) third.size else 10
        )
    )
    while (first.hasNext() && second.hasNext() && third.hasNext()) {
        list.add(Triple(first.next(), second.next(), third.next()))
    }
    return list
}

fun <T> MutableList<T>.replaceAllIndexed(transform: (index: Int, source: T) -> T) {
    for (i in this.indices) {
        this[i] = transform(i, this[i])
    }
}

class LabelTextPropertyBinder(private val stringProperty: StringProperty, private val value: FloatProperty) {
    infix fun with(valueConverter: ValueConverter) {
        stringProperty.bind(
            Bindings.createStringBinding(
                { valueConverter.toString(value.get()) }, value
            )
        )

    }
}

infix fun StringProperty.bindTo(value: FloatProperty) = LabelTextPropertyBinder(this, value)

typealias ObservablePosition = Pair<
        @Serializable(FloatPropertySerializer::class) FloatProperty,
        @Serializable(FloatPropertySerializer::class) FloatProperty
        >

fun ObservablePosition() = Pair(SimpleFloatProperty(0f), SimpleFloatProperty(0f))