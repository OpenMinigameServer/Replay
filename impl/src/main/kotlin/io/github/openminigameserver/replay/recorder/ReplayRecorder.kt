package io.github.openminigameserver.replay.recorder

import io.github.openminigameserver.replay.ReplayManager
import io.github.openminigameserver.replay.extensions.toReplay
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.model.recordable.impl.RecEntitiesPosition
import io.github.openminigameserver.replay.model.recordable.impl.RecEntityMove
import io.github.openminigameserver.replay.recorder.PositionRecordType.*
import net.minestom.server.MinecraftServer
import net.minestom.server.data.DataImpl
import net.minestom.server.instance.Instance
import net.minestom.server.timer.Task
import net.minestom.server.utils.Position
import net.minestom.server.utils.time.TimeUnit
import java.util.*

class ReplayRecorder(private val instance: Instance, private val options: RecorderOptions = RecorderOptions()) {
    private var tickerTask: Task
    val replay = ReplayManager.createEmptyReplay()
    var isRecording = false

    init {
        if (instance.data == null) {
            instance.data = DataImpl()
        }
        tickerTask = buildTickerTask()
    }

    private fun buildTickerTask(): Task {
        val entityPositions = mutableMapOf<UUID, Position>()

        return MinecraftServer.getSchedulerManager().buildTask {
            if (!isRecording) return@buildTask
            doEntityTick(entityPositions)
        }.repeat(1, TimeUnit.TICK).schedule()
    }

    private fun doEntityTick(entityPositions: MutableMap<UUID, Position>) {
        val recordedPositions = mutableMapOf<RecordableEntity, RecordablePosition>()

        instance.entities.forEach { entity ->
            if (entity.instance != instance || !isRecording) return@forEach
            val currentPosition = entity.position

            val oldPosition = entityPositions[entity.uuid]
            if (oldPosition == null || oldPosition != currentPosition) {
                val replayEntity = replay.getEntityById(entity.entityId)
                recordedPositions[replayEntity] = currentPosition.toReplay()
                entityPositions[entity.uuid] = entity.position.clone()
            }
        }

        when (options.positionRecordType) {
            GROUP_ALL -> listOf(RecEntitiesPosition(recordedPositions))
            SEPARATE_ALL -> recordedPositions.map { RecEntityMove(it.value, it.key) }
        }.forEach {
            if (it !is RecEntitiesPosition || it.positions.isNotEmpty())
                replay.addAction(it)
        }
    }

    fun startRecording() {
        instance.entities.forEach { minestomEntity ->
            //Save all entities
            replay.apply {
                val entity = minestomEntity.toReplay()
                entities[minestomEntity.entityId] = entity
            }
        }
        isRecording = true
    }

    fun stopRecording() {
        isRecording = false
        tickerTask.cancel()
    }
}

