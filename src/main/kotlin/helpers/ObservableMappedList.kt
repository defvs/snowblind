import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList

class ObservableMappedList<S, D> private constructor(
    private val source: ObservableList<S>,
    transform: (index: Int, item: S) -> D,
    private val dest: ObservableList<D>,
) : ObservableList<D> by dest {

    constructor(source: ObservableList<S>, transform: (index: Int, item: S) -> D) : this(
        source,
        transform,
        FXCollections.observableArrayList()
    )

    init {
        // Initialize the destination list with transformed items from the source list
        dest.setAll(source.mapIndexed(transform))

        // Add a listener to the source list to handle changes
        source.addListener { change: ListChangeListener.Change<out S> ->
            while (change.next()) {
                when {
                    change.wasPermutated() -> {
                        val permutedDest = dest.toMutableList()
                        for (i in change.from..<change.to) {
                            permutedDest[i] = dest[change.getPermutation(i)]
                        }
                        dest.setAll(permutedDest)
                    }

                    change.wasUpdated() -> {
                        for (i in change.from..<change.to) {
                            dest[i] = transform(i, source[i])
                        }
                    }

                    change.wasReplaced() -> {
                        for (i in change.from..<change.to) {
                            dest[i] = transform(i, source[i])
                        }
                    }

                    change.wasRemoved() -> {
                        dest.remove(change.from, change.from + change.removedSize)
                    }

                    change.wasAdded() -> {
                        val startIndex = change.from
                        val addedItems = change.addedSubList.mapIndexed { index, item ->
                            transform(startIndex + index, item)
                        }
                        dest.addAll(startIndex, addedItems)
                    }
                }
            }
        }
    }
}
