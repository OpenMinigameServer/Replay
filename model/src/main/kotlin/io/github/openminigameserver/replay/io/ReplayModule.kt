package io.github.openminigameserver.replay.io

import com.fasterxml.jackson.databind.module.SimpleModule
import io.github.openminigameserver.replay.io.duration.KotlinDurationDeserializer
import io.github.openminigameserver.replay.io.duration.KotlinDurationSerializer
import io.github.openminigameserver.replay.io.instant.KotlinInstantDeserializer
import io.github.openminigameserver.replay.io.instant.KotlinInstantSerializer
import kotlinx.datetime.Instant
import kotlin.time.Duration

class ReplayModule : SimpleModule() {
    init {
        addSerializer(KotlinInstantSerializer())
        addDeserializer(Instant::class.java, KotlinInstantDeserializer())

        addSerializer(KotlinDurationSerializer())
        addDeserializer(Duration::class.java, KotlinDurationDeserializer())
    }
}

