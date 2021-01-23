package io.github.openminigameserver.replay.io.duration

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import kotlin.time.Duration
import kotlin.time.milliseconds

class KotlinDurationDeserializer : StdDeserializer<Duration>(Duration::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Duration {
        return p.doubleValue.milliseconds
    }
}