package helpers

import java.util.*

@JvmInline
value class NodeUUID(val uuid: UUID = UUID.randomUUID())

@JvmInline
value class ConnectorUUID(val uuid: UUID = UUID.randomUUID())

@JvmInline
value class ClipUUID(val uuid: UUID = UUID.randomUUID())
