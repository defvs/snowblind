package nodes

import java.util.*

sealed class NodeConnection(val source: UUID, val dest: UUID) {
    class LaserConnection(source: UUID, dest: UUID) : NodeConnection(source, dest)
    class ParameterConnection(source: UUID, dest: UUID) : NodeConnection(source, dest)
}