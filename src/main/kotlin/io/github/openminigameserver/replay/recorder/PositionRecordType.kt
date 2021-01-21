package io.github.openminigameserver.replay.recorder

enum class PositionRecordType {
    /**
     * Group recorded positions into one Recordable
     */
    GROUP_ALL,

    /**
     * Split recorded positions into multiple Recordables
     */
    SEPARATE_ALL
}
