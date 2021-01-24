package io.github.openminigameserver.replay.model.recordable

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeInfo
import kotlin.time.Duration

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@type")
abstract class RecordableAction {
    var timestamp: Duration = Duration.ZERO

    @get:JsonIgnore
    open val isReversible: Boolean
        get() = false

    @get:JsonIgnore
    open val reversedAction: List<RecordableAction>
        get() = listOf(this)
}
