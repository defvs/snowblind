package helpers

import javafx.beans.binding.Bindings
import javafx.beans.property.FloatProperty
import javafx.beans.property.StringProperty
import nodes.ValueConverter

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

fun <K, V> zipMapOfList(
    map1: Map<K, List<V>>,
    map2: Map<K, List<V>>,
): Map<K, List<V>> {
    return (map1.asSequence() + map2.asSequence())
        .groupBy({ it.key }, { it.value })
        .mapValues { (_, values) -> values.flatten() }
}