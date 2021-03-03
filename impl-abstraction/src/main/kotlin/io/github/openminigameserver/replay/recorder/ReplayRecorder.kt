package io.github.openminigameserver.replay.recorder

import io.github.openminigameserver.replay.ReplayManager
import io.github.openminigameserver.replay.TickTime
import io.github.openminigameserver.replay.TimeUnit
import io.github.openminigameserver.replay.abstraction.ReplayEntity
import io.github.openminigameserver.replay.abstraction.ReplayWorld
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.RecordablePositionAndVector
import io.github.openminigameserver.replay.model.recordable.RecordedChunk
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.model.recordable.impl.*
import io.github.openminigameserver.replay.platform.ReplayExtension
import io.github.openminigameserver.replay.recorder.PositionRecordType.GROUP_ALL
import io.github.openminigameserver.replay.recorder.PositionRecordType.SEPARATE_ALL
import java.util.*

class ReplayRecorder(
    private val replayExtension: ReplayExtension,
    private val instance: ReplayWorld,
    private val options: RecorderOptions = RecorderOptions(),
    private val tickInterval: TickTime = TickTime(1L, TimeUnit.TICK)
) {
    private val replayManager: ReplayManager
        get() = replayExtension.replayManager
    private var tickerTask: Any
    val replay = replayManager.createEmptyReplay()
    private var isRecording = false

    init {
        tickerTask = buildTickerTask()
    }

    fun onHandSwing(entity: RecordableEntity, hand: Hand) {
        replay.addAction(RecPlayerHandAnimation(hand, entity))
    }

    fun onEntityRemove(entity: RecordableEntity, position: RecordablePosition) {
        replay.addAction(RecEntityRemove(position, entity))
    }

    fun onEntitySpawn(entity: RecordableEntity, positionAndVector: RecordablePositionAndVector) {
        replay.entities[entity.id] = entity
        replay.addAction(
            RecEntitySpawn(
                positionAndVector, entity
            )
        )
    }

    fun onEntityEquipmentChange(entity: ReplayEntity) {
        val replayEntity = replay.getEntityById(entity.id) ?: return

        replay.addAction(RecEntityEquipmentUpdate(replayEntity, entity.getEquipment()))
    }

    private fun buildTickerTask(): Any {
        val entityPositions = mutableMapOf<UUID, RecordablePositionAndVector>()

        return replayExtension.platform.registerSyncRepeatingTask(tickInterval) {
            doEntityTick(entityPositions)
        }
    }

    private fun doEntityTick(entityPositions: MutableMap<UUID, RecordablePositionAndVector>) {
        val recordedPositions = mutableMapOf<RecordableEntity, RecordablePositionAndVector>()

        instance.entities.forEach { entity ->
            if (entity.instance != instance || !isRecording) return@forEach
            val currentPosition = entity.position

            val oldPosition = entityPositions[entity.uuid]
            val currentNewPosition =
                RecordablePositionAndVector(currentPosition, entity.velocity)
            if (oldPosition == null || oldPosition != currentNewPosition || options.recordAllLocationChanges) {
                val replayEntity = replay.getEntityById(entity.id) ?: return@forEach
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
        recordInstanceIfNeeded()
        instance.entities.forEach { entity ->
            //Save all entities
            replay.apply {
                val entity = entity
                entities[entity.id] = entity.toReplay(replayExtension.platform)
            }
        }
        isRecording = true
    }

    private fun recordInstanceIfNeeded() {
        if (!options.recordInstanceChunks) return

        instance.chunks.forEach {
            replay.chunks.add(
                RecordedChunk(
                    it.x,
                    it.z,
                    it.serializedData
                        ?: throw Exception(
                            "Unable to serialize chunk of type ${it.javaClass.name}.\n" +
                                    "Please make sure that your chunks can be serialized if you want to save them in the replay."
                        )
                )
            )

        }
    }

    fun stopRecording() {
        replay.duration = replay.currentDuration
        isRecording = false
        replayExtension.platform.cancelTask(tickerTask)
    }

    fun notifyBlockChange(
        x: Int,
        y: Int,
        z: Int,
        newState: Short
    ) {
        replay.addAction(
            RecBlockStateUpdate(
                RecordablePosition(x.toDouble(), y.toDouble(), z.toDouble(), 0f, 0f),
                newState
            )
        )
    }

}


