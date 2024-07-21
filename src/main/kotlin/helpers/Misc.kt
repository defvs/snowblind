package helpers

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