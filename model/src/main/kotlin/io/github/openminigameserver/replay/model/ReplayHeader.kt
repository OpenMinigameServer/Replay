package io.github.openminigameserver.replay.model

import io.github.openminigameserver.replay.model.recordable.RecordedChunk
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.*
import kotlin.time.Duration

open class ReplayHeader(
    open var version: Int = 4,
    open var id: UUID = UUID.randomUUID(),
    open var recordStartTime: Instant = Clock.System.now()
) {
    var metadata: MutableMap<String, Any> = mutableMapOf()
    var duration: Duration = Duration.ZERO
    var chunks = mutableListOf<RecordedChunk>()

    val hasChunks get() = chunks.isNotEmpty()

    operator fun <T : Any> get(name: String): T? {
        return metadata[name] as? T
    }

    operator fun <T : Any> set(name: String, value: T) {
        metadata[name] = value
    }
}