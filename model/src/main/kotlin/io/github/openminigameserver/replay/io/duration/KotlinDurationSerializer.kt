package io.github.openminigameserver.replay.io.duration

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import kotlin.time.Duration

class KotlinDurationSerializer : StdSerializer<Duration>(Duration::class.java) {
    override fun serialize(value: Duration, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeNumber(value.inMilliseconds)
    }
}