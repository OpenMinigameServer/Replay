package io.github.openminigameserver.replay

enum class TimeUnit {
    TICK, DAY, HOUR, MINUTE, SECOND, MILLISECOND;

    fun toMilliseconds(value: Long): Long {
        return when (this) {
            TICK -> 50 * value
            DAY -> value * 86400000
            HOUR -> value * 3600000
            MINUTE -> value * 60000
            SECOND -> value * 1000
            MILLISECOND -> value
            else -> -1 // Unexpected
        }
    }
}