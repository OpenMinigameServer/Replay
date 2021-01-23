package io.github.openminigameserver.replay.io.entity

import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.KeyDeserializer
import io.github.openminigameserver.replay.model.Replay
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

class EntityKeyDeserializer : KeyDeserializer() {
    override fun deserializeKey(key: String?, ctxt: DeserializationContext): Any {
        var parent = ctxt.parser.parsingContext.parent
        while (parent.parent != null) {
            parent = parent.parent
        }

        val currentNodeMethod =
            MethodHandles.lookup().bind(parent, "currentNode", MethodType.methodType(JsonNode::class.java))
        val currentNode = currentNodeMethod.invoke() as? JsonNode ?: throw Exception("Unable to find parent node")

        val entities = currentNode.get(Replay::entities.name) ?: throw Exception("Unable to find entity with Id $key")

        return ctxt.parser.codec.treeToValue(entities.get(key), RecordableEntity::class.java)
    }
}