@file:UseSerializers(
    FloatPropertySerializer::class
)

package nodes.implementations.generators

import com.github.ajalt.colormath.model.RGB
import helpers.ConnectorUUID
import helpers.NodeUUID
import helpers.ObservablePosition
import helpers.serialization.javafx.FloatPropertySerializer
import helpers.serialization.nodes.GeneratorNodeSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import laser.LaserObject
import laser.Point
import nodes.GeneratorNode
import nodes.helpers.SimpleValueConverters
import nodes.helpers.SimpleValueRanges
import nodes.mapToInput
import nodes.parameters
import ui.nodes.controls.SliderControl


@Serializable(with = PointGeneratorNodeSerializer::class)
class PointGeneratorNode(
    override val uuid: NodeUUID = NodeUUID(),
    override val laserOutputUUID: ConnectorUUID = ConnectorUUID(),
    existingUUIDs: List<ConnectorUUID>? = null,
    existingValues: Map<ConnectorUUID, Float>? = null,
    override val position: ObservablePosition = ObservablePosition(),
) : GeneratorNode {

    override val name: String = "Point Generator"
    override val description: String = """
        Generates a single point
    """.trimIndent()

    override val parameters = parameters {
        internalControllable(
            name = "X",
            range = SimpleValueRanges.position,
            valueConverter = SimpleValueConverters.AsInteger,
            control = SliderControl()
        )
        internalControllable(
            name = "Y",
            range = SimpleValueRanges.position,
            valueConverter = SimpleValueConverters.AsInteger,
            control = SliderControl()
        )
        internalControllable(
            name = "Red",
            range = SimpleValueRanges.color,
            valueConverter = SimpleValueConverters.As8bitColor,
            control = SliderControl()
        )
        internalControllable(
            name = "Green",
            range = SimpleValueRanges.color,
            valueConverter = SimpleValueConverters.As8bitColor,
            control = SliderControl()
        )
        internalControllable(
            name = "Blue",
            range = SimpleValueRanges.color,
            valueConverter = SimpleValueConverters.As8bitColor,
            control = SliderControl()
        )

        existingUUIDs?.let { withExistingUUIDs(it) }
        existingValues?.let { withExistingValues(it) }
    }

    override fun computeLaser(inputParameters: Map<ConnectorUUID, Float>): List<LaserObject> {
        val data = parameters.mapToInput(inputParameters)

        return listOf(
            LaserObject(
                Point(
                    data["X"]!!,
                    data["Y"]!!,
                ), RGB(
                    data["Red"]!!,
                    data["Green"]!!,
                    data["Blue"]!!,
                )
            )
        )
    }
}

class PointGeneratorNodeSerializer : GeneratorNodeSerializer<PointGeneratorNode>({
    PointGeneratorNode(
        it.uuid,
        it.laserOutputUUID,
        it.parametersUUIDs,
        it.internalParametersValues,
        it.position
    )
}, PointGeneratorNode::class)
