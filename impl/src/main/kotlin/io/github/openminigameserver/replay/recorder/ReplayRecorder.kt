package io.github.openminigameserver.replay.recorder

import io.github.openminigameserver.replay.ReplayManager
import io.github.openminigameserver.replay.TickTime
import io.github.openminigameserver.replay.extensions.toReplay
import io.github.openminigameserver.replay.model.recordable.RecordablePositionAndVector
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.model.recordable.impl.RecEntitiesPosition
import io.github.openminigameserver.replay.model.recordable.impl.RecEntityMove
import io.github.openminigameserver.replay.model.recordable.impl.RecEntityRemove
import io.github.openminigameserver.replay.model.recordable.impl.RecEntitySpawn
import io.github.openminigameserver.replay.recorder.PositionRecordType.*
import net.minestom.server.MinecraftServer
import net.minestom.server.data.DataImpl
import net.minestom.server.event.entity.EntitySpawnEvent
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent
import net.minestom.server.instance.Instance
import net.minestom.server.timer.Task
import net.minestom.server.utils.time.TimeUnit
import java.util.*

class ReplayRecorder(
    private val instance: Instance,
    private val options: RecorderOptions = RecorderOptions(),
    private val tickInterval: TickTime = TickTime(1L, TimeUnit.TICK)
) {
    private var tickerTask: Task
    val replay = ReplayManager.createEmptyReplay()
    private var isRecording = false

    init {
        if (instance.data == null) {
            instance.data = DataImpl()
        }
        tickerTask = buildTickerTask()
        registerListeners()
    }


    lateinit var entitySpawnHandler: (event: EntitySpawnEvent) -> Unit
    lateinit var removeEntityFromInstanceHandler: (event: RemoveEntityFromInstanceEvent) -> Unit
    private fun registerListeners() {
        initListeners()
        instance.addEventCallback(EntitySpawnEvent::class.java, entitySpawnHandler)
        instance.addEventCallback(RemoveEntityFromInstanceEvent::class.java, removeEntityFromInstanceHandler)
    }

    private fun removeListeners() {
        instance.removeEventCallback(EntitySpawnEvent::class.java, entitySpawnHandler)
        instance.removeEventCallback(RemoveEntityFromInstanceEvent::class.java, removeEntityFromInstanceHandler)
    }

    private fun initListeners() {
        removeEntityFromInstanceHandler = {
            val minestomEntity = it.entity
            val entity = replay.getEntityById(minestomEntity.entityId)
            replay.addAction(RecEntityRemove(minestomEntity.position.toReplay(), entity))
        }

        entitySpawnHandler = {
            val minestomEntity = it.entity
            val entity = minestomEntity.toReplay(false)
            replay.entities[entity.id] = entity
            replay.addAction(RecEntitySpawn(minestomEntity.position.toReplay(), entity))
        }
    }

    private fun buildTickerTask(): Task {
        val entityPositions = mutableMapOf<UUID, RecordablePositionAndVector>()

        return MinecraftServer.getSchedulerManager().buildTask {
            if (!isRecording) return@buildTask
            doEntityTick(entityPositions)
        }.repeat(tickInterval.time, tickInterval.unit).schedule()
    }

    private fun doEntityTick(entityPositions: MutableMap<UUID, RecordablePositionAndVector>) {
        val recordedPositions = mutableMapOf<RecordableEntity, RecordablePositionAndVector>()

        instance.entities.forEach { entity ->
            if (entity.instance != instance || !isRecording) return@forEach
            val currentPosition = entity.position

            val oldPosition = entityPositions[entity.uuid]
            val currentNewPosition =
                RecordablePositionAndVector(currentPosition.toReplay(), entity.velocity.toReplay())
            if (oldPosition == null || oldPosition != currentNewPosition || options.recordAllChanges) {
                val replayEntity = replay.getEntityById(entity.entityId)
                recordedPositions[replayEntity] = currentNewPosition
                entityPositions[entity.uuid] = currentNewPosition
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
        replay.duration = replay.currentDuration
        isRecording = false
        tickerTask.cancel()
        removeListeners()
    }
}

