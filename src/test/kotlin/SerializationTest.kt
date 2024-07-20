package laser

import clips.Clip
import clips.GeneratorClip
import helpers.serialization.nodeSerializersModule
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import nodes.INodeBase
import nodes.NodeConnection
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
        inputParams[0] = 1.0f
    }
    val node2 = PositionOffsetTransformNode().apply {
        inputParams[0] = 3.0f
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
        node4.uuid, node4.outputParams[0].uuid,
        node2.uuid, node2.inputParams[1].uuid
    )

    val connectionList = listOf(connection1, connection2, connection3)

    val clip: Clip = GeneratorClip("Clip1").apply {
        this.nodes.putAll(nodesList.associateBy { it.uuid })
        connectionList.forEach { this.connectionMap += it }
    }

    describe("Clip serialization and deserialization") {
        it("should serialize and deserialize clip without exceptions") {
            val output = jsonEncoder.encodeToString(clip).also { println("Deserialized clip:\n${it}") }
            val deserializedClip = jsonEncoder.decodeFromString<Clip>(output) as GeneratorClip

            deserializedClip.name shouldBe "Clip1"
            deserializedClip.nodes.size shouldBe clip.nodes.size
            deserializedClip.connectionMap.connections.size shouldBe clip.connectionMap.connections.size

            clip.nodes.keys.forEach { uuid ->
                deserializedClip.nodes.containsKey(uuid) shouldBe true
            }

            clip.connectionMap.connections.forEach { connection ->
                deserializedClip.connectionMap.connections.contains(connection) shouldBe true
            }
        }

        it("should maintain UUID equality after serialization and deserialization") {
            val output = jsonEncoder.encodeToString(clip)
            val deserializedClip = jsonEncoder.decodeFromString<Clip>(output) as GeneratorClip

            clip.nodes.keys shouldBe deserializedClip.nodes.keys
            clip.connectionMap.connections.map { it.source.nodeUUID to it.dest.nodeUUID } shouldBe deserializedClip.connectionMap.connections.map { it.source.nodeUUID to it.dest.nodeUUID }
        }
    }

    describe("Node serialization and deserialization") {
        nodesList.forEach { node ->
            it("should serialize and deserialize ${node::class.simpleName} without exceptions") {
                val output = jsonEncoder.encodeToString(node)
                val deserializedNode = jsonEncoder.decodeFromString<INodeBase>(output)

                deserializedNode.uuid shouldBe node.uuid
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
