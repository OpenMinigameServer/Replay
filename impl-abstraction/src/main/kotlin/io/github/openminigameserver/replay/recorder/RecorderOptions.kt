package io.github.openminigameserver.replay.recorder

data class RecorderOptions(
    val positionRecordType: PositionRecordType = PositionRecordType.GROUP_ALL,
    val recordAllLocationChanges: Boolean = true,
    val recordInstanceChunks: Boolean = true
)
