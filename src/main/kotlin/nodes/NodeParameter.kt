package nodes

import helpers.ConnectorUUID
import kotlinx.serialization.Serializable

typealias ReadableValueConverter = (Float) -> String

/**
 * Data class representing a node parameter with a UUID and associated data.
 */
@Serializable
data class NodeParameter(
    val uuid: ConnectorUUID = ConnectorUUID(),
    var data: Float = 0.0f,
)

class NodeParameterDefinition(
    val name: String,
    val range: ClosedFloatingPointRange<Float>,
    val valueConverter: ReadableValueConverter,
)