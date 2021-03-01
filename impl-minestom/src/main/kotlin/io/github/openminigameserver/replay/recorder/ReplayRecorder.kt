package io.github.openminigameserver.replay.recorder

import io.github.openminigameserver.replay.ReplayManager
import io.github.openminigameserver.replay.TickTime
import io.github.openminigameserver.replay.extensions.getEntity
import io.github.openminigameserver.replay.extensions.getEquipmentForEntity
import io.github.openminigameserver.replay.extensions.recorder
import io.github.openminigameserver.replay.extensions.toReplay
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.RecordablePositionAndVector
import io.github.openminigameserver.replay.model.recordable.RecordedChunk
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.model.recordable.impl.*
import io.github.openminigameserver.replay.recorder.PositionRecordType.GROUP_ALL
import io.github.openminigameserver.replay.recorder.PositionRecordType.SEPARATE_ALL
import net.minestom.server.MinecraftServer
import net.minestom.server.data.DataImpl
import net.minestom.server.entity.Entity
import net.minestom.server.event.entity.EntitySpawnEvent
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent
import net.minestom.server.event.player.PlayerHandAnimationEvent
import net.minestom.server.instance.Instance
import net.minestom.server.inventory.EquipmentHandler
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


    lateinit var handAnimationHandler: (event: PlayerHandAnimationEvent) -> Unit
    lateinit var entitySpawnHandler: (event: EntitySpawnEvent) -> Unit
    lateinit var removeEntityFromInstanceHandler: (event: RemoveEntityFromInstanceEvent) -> Unit
    private fun initListeners() {
        handAnimationHandler = eventCallback@{ event: PlayerHandAnimationEvent ->
            val replay = event.player.instance?.takeIf { it.uniqueId == instance.uniqueId }?.recorder?.replay
                ?: return@eventCallback

            replay.getEntity(event.player)
                ?.let { replay.addAction(RecPlayerHandAnimation(enumValueOf(event.hand.name), it)) }
        }

        removeEntityFromInstanceHandler = event@{
            val minestomEntity = it.entity.takeIf { e -> e.instance?.uniqueId == instance.uniqueId } ?: return@event
            val entity = replay.getEntityById(minestomEntity.entityId) ?: return@event
            replay.addAction(RecEntityRemove(minestomEntity.position.toReplay(), entity))
        }

        entitySpawnHandler = event@{
            val minestomEntity = it.entity.takeIf { e -> e.instance?.uniqueId == instance.uniqueId } ?: return@event
            val entity = replay.getEntityById(minestomEntity.entityId) ?: minestomEntity.toReplay(false)
            replay.entities[entity.id] = entity
            replay.addAction(
                RecEntitySpawn(
                    RecordablePositionAndVector(
                        minestomEntity.position.toReplay(),
                        minestomEntity.velocity.toReplay()
                    ), entity
                )
            )
        }
    }

    private fun registerListeners() {
        initListeners()
        instance.addEventCallback(PlayerHandAnimationEvent::class.java, handAnimationHandler)
        instance.addEventCallback(EntitySpawnEvent::class.java, entitySpawnHandler)
        instance.addEventCallback(RemoveEntityFromInstanceEvent::class.java, removeEntityFromInstanceHandler)
    }

    private fun removeListeners() {
        instance.removeEventCallback(PlayerHandAnimationEvent::class.java, handAnimationHandler)
        instance.removeEventCallback(EntitySpawnEvent::class.java, entitySpawnHandler)
        instance.removeEventCallback(RemoveEntityFromInstanceEvent::class.java, removeEntityFromInstanceHandler)
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
            if (oldPosition == null || oldPosition != currentNewPosition || options.recordAllLocationChanges) {
                val replayEntity = replay.getEntityById(entity.entityId) ?: return@forEach
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
        instance.entities.forEach { minestomEntity ->
            //Save all entities
            replay.apply {
                val entity = minestomEntity.toReplay()
                entities[minestomEntity.entityId] = entity
            }
        }
        isRecording = true
    }

    private fun recordInstanceIfNeeded() {
        if (!options.recordInstanceChunks) return

        instance.chunks.forEach {
            replay.chunks.add(
                RecordedChunk(
                    it.chunkX,
                    it.chunkZ,
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
        tickerTask.cancel()
        removeListeners()
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

    fun notifyEntityEquipmentChange(entity: Entity) {
        if (entity !is EquipmentHandler) return
        val replayEntity = replay.getEntity(entity) ?: return

        replay.addAction(RecEntityEquipmentUpdate(replayEntity, entity.getEquipmentForEntity()))
    }

}


