package helpers

import helpers.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
@JvmInline
value class NodeUUID(@Serializable(with = UUIDSerializer::class) val uuid: UUID = UUID.randomUUID())

@Serializable
@JvmInline
value class ConnectorUUID(@Serializable(with = UUIDSerializer::class) val uuid: UUID = UUID.randomUUID())

@Serializable
@JvmInline
value class ClipUUID(@Serializable(with = UUIDSerializer::class) val uuid: UUID = UUID.randomUUID())

@Serializable
@JvmInline
value class ClipRackUUID(@Serializable(with = UUIDSerializer::class) val uuid: UUID = UUID.randomUUID())
