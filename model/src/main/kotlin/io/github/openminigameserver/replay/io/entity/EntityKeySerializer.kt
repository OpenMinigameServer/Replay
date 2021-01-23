package io.github.openminigameserver.replay.io.entity

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity

class EntityKeySerializer : StdSerializer<RecordableEntity>(RecordableEntity::class.java) {
    override fun serialize(value: RecordableEntity, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeFieldName(value.id.toString())
    }
}

