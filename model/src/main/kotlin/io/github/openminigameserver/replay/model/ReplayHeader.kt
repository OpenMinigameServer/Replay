package io.github.openminigameserver.replay.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.*
import kotlin.time.Duration

open class ReplayHeader(
    open var version: Int = 3,
    open var id: UUID = UUID.randomUUID(),
    open var recordStartTime: Instant = Clock.System.now()
) {
    var metadata: MutableMap<String, Any> = mutableMapOf()
    var duration: Duration = Duration.ZERO
    operator fun <T : Any> get(name: String): T? {
        return metadata[name] as? T
    }

    operator fun <T : Any> set(name: String, value: T) {
        metadata[name] = value
    }
}