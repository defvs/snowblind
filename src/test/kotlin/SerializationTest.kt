package laser

import clips.Clip
import clips.GeneratorClip
import helpers.serialization.nodeSerializersModule
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import nodes.INodeBase
import nodes.NodeConnection
import nodes.NodeParameter
import nodes.implementations.generators.PointGeneratorNode
import nodes.implementations.special.MacroNode
import nodes.implementations.special.OutputNode
import nodes.implementations.transforms.PositionOffsetTransformNode

@OptIn(ExperimentalSerializationApi::class)
class SerializationTest : DescribeSpec({

    val jsonEncoder = Json {
        prettyPrint = true
        serializersModule = nodeSerializersModule
        namingStrategy = JsonNamingStrategy.SnakeCase
    }

    val node1 = PointGeneratorNode().apply {
        parameters[0] = 1.4f
        parameters[1] = -10f
    }
    val node2 = PositionOffsetTransformNode().apply {
        parameters[0] = 3.0f
    }
    val node3 = OutputNode()
    val node4 = MacroNode()

    val nodesList = listOf(node1, node2, node3, node4)

    val connection1 = NodeConnection(
        node1.uuid, node1.laserOutputUUID,
        node2.uuid, node2.laserInputUUID
    )
    val connection2 = NodeConnection(
        node2.uuid, node2.laserOutputUUID,
        node3.uuid, node3.laserInputUUID
    )
    val connection3 = NodeConnection(
        node4.uuid,
        node4.parameters.parameters.filterIsInstance<NodeParameter.OutputParameter>()[0].uuid,
        node2.uuid,
        node2.parameters.parameters.filterIsInstance<NodeParameter.ControllableParameter.ControllableInputParameter>()[1].uuid
    )

    val connectionList = listOf(connection1, connection2, connection3)

    val clip: Clip = GeneratorClip("Clip1").apply {
        this.nodes.putAll(nodesList.associateBy { it.uuid })
        connectionList.forEach { this.connectionMap += it }
    }

    describe("Clip serialization and deserialization") {
        val serializedClip = jsonEncoder.encodeToString(clip).also { println("Deserialized clip:\n${it}") }
        val deserializedClip = jsonEncoder.decodeFromString<Clip>(serializedClip) as GeneratorClip

        it("should serialize and deserialize clip without exceptions") {}

        it("should maintain UUID equality after serialization and deserialization") {
            deserializedClip.uuid shouldBeEqual clip.uuid
        }

        it("nodes count should be the same") {
            deserializedClip.nodes.size shouldBeEqual clip.nodes.size
        }

        it("connections count should be the same") {
            deserializedClip.connectionMap.connections.size shouldBeEqual clip.connectionMap.connections.size
        }
    }

    describe("Node serialization and deserialization") {
        val serializedNodes = nodesList.map { jsonEncoder.encodeToString(it) }
        val deserializedNodes = serializedNodes.map { jsonEncoder.decodeFromString<INodeBase>(it) }
        deserializedNodes.zip(nodesList).forEach { (deserializedNode, originalNode) ->
            describe("for node ${originalNode.name} uuid=${originalNode.uuid.uuid}") {
                it("should serialize and deserialize without exceptions") {}
                it("should maintain UUID equality after serialization and deserialization") {
                    deserializedNode.uuid shouldBeEqual originalNode.uuid
                }
                it("should maintain name and description equality after serialization and deserialization") {
                    deserializedNode.name shouldBeEqual originalNode.name
                    if (deserializedNode.description != null)
                        deserializedNode.description!! shouldBeEqual originalNode.description!!
                }
                it("should maintain parameter types equality after serialization and deserialization") {
                    deserializedNode.parameters.parameters.zip(originalNode.parameters.parameters)
                        .forEach { (deserializedParameter, originalParameter) ->
                            deserializedParameter::class shouldBeEqual originalParameter::class
                        }
                }
                it("should maintain parameter UUIDs and name equality after serialization and deserialization") {
                    deserializedNode.parameters.parameters.zip(originalNode.parameters.parameters)
                        .forEach { (deserializedParameter, originalParameter) ->
                            deserializedParameter.uuid shouldBeEqual originalParameter.uuid
                            deserializedParameter.name shouldBeEqual originalParameter.name
                        }
                }
                it("when ControllableParameter, should maintain value equality after serialization and deserialization") {
                    deserializedNode.parameters.parameters.zip(originalNode.parameters.parameters)
                        .mapNotNull { (deserializedParameter, originalParameter) ->
                            val first =
                                deserializedParameter as? NodeParameter.ControllableParameter ?: return@mapNotNull null
                            val second =
                                originalParameter as? NodeParameter.ControllableParameter ?: return@mapNotNull null
                            first to second
                        }
                        .forEach { (deserializedParameter, originalParameter) ->
                            deserializedParameter.defaultValue shouldBeEqual originalParameter.defaultValue
                            deserializedParameter.value shouldBeEqual originalParameter.value
                        }
                }
            }
        }
    }

    describe("Connection serialization and deserialization") {
        connectionList.forEach { connection ->
            it("should serialize and deserialize connection from ${connection.source.nodeUUID} to ${connection.dest.nodeUUID} without exceptions") {
                val output = jsonEncoder.encodeToString(connection)
                val deserializedConnection = jsonEncoder.decodeFromString<NodeConnection>(output)

                deserializedConnection.source.nodeUUID shouldBe connection.source.nodeUUID
                deserializedConnection.source.connectorUUID shouldBe connection.source.connectorUUID
                deserializedConnection.dest.nodeUUID shouldBe connection.dest.nodeUUID
                deserializedConnection.dest.connectorUUID shouldBe connection.dest.connectorUUID
            }
        }
    }
})
