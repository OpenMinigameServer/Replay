package io.github.openminigameserver.replay.extensions

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import io.github.openminigameserver.replay.model.Replay
import io.github.openminigameserver.replay.model.recordable.RecordableItemStack
import io.github.openminigameserver.replay.model.recordable.RecordablePosition
import io.github.openminigameserver.replay.model.recordable.RecordableVector
import io.github.openminigameserver.replay.model.recordable.entity.EntityEquipmentSlot
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.model.recordable.entity.data.BaseEntityData
import io.github.openminigameserver.replay.model.recordable.entity.data.PlayerEntityData
import io.github.openminigameserver.replay.model.recordable.entity.data.PlayerSkinData
import io.github.openminigameserver.replay.player.ReplaySession
import io.github.openminigameserver.replay.recorder.ReplayRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.instance.Instance
import net.minestom.server.inventory.EquipmentHandler
import net.minestom.server.item.ItemStack
import net.minestom.server.network.packet.server.play.EntityEquipmentPacket
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket
import net.minestom.server.utils.NBTUtils
import net.minestom.server.utils.Position
import net.minestom.server.utils.Vector
import net.minestom.server.utils.binary.BinaryReader
import net.minestom.server.utils.binary.BinaryWriter
import java.util.*
import java.util.concurrent.TimeUnit

fun Position.toReplay(): RecordablePosition = RecordablePosition(x, y, z, yaw, pitch)
fun RecordablePosition.toMinestom(): Position = Position(x, y, z, yaw, pitch)

fun Vector.toReplay(): RecordableVector = RecordableVector(x, y, z)
fun RecordableVector.toMinestom(): Vector = Vector(x, y, z)

fun ItemStack.toReplay(): RecordableItemStack =
    RecordableItemStack(BinaryWriter().also { NBTUtils.writeItemStack(it, this) }.toByteArray())

fun RecordableItemStack.toMinestom(): ItemStack =
    BinaryReader(nbtValue).let { NBTUtils.readItemStack(it) ?: ItemStack.getAirItem() }

const val REPLAY_RECORDER_DATA = "replay:recorder"
const val REPLAY_REPLAYER_DATA = "replay:replayer"

var Player.recorder: ReplayRecorder?
    get() = instance!!.recorder
    set(value) {
        instance!!.recorder = value
    }

var Instance.recorder: ReplayRecorder?
    get() = data?.get(REPLAY_RECORDER_DATA)
    set(value) {
        data?.set(REPLAY_RECORDER_DATA, value)
    }

var Instance.replaySession: ReplaySession?
    get() = data?.get(REPLAY_REPLAYER_DATA)
    set(value) {
        data?.set(REPLAY_REPLAYER_DATA, value)
    }

internal fun runOnSeparateThread(code: suspend CoroutineScope.() -> Unit) {
    Thread {
        runBlocking(block = code)
    }.start()
}

internal val profileCache: Cache<UUID, PlayerSkin> =
    CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build()

internal fun Entity.toReplay(spawnOnStart: Boolean = true): RecordableEntity {
    var data: BaseEntityData? = null
    if (this is Player) {
        val skin = skin

        data = PlayerEntityData(username, skin?.toReplay(), metadataPacket.getMetadataArray(), getEquipmentForEntity())
    }
    return RecordableEntity(entityId, entityType.namespaceID, position.toReplay(), data).apply {
        this.spawnOnStart =
            spawnOnStart
    }
}

internal fun PlayerSkin.toReplay(): PlayerSkinData {
    val decoder = Base64.getDecoder()
    return PlayerSkinData(decoder.decode(textures), decoder.decode(signature))
}

fun PlayerSkinData.toMinestom(): PlayerSkin {
    val encoder = Base64.getEncoder()
    return PlayerSkin(encoder.encodeToString(textures), encoder.encodeToString(signature))
}


fun Replay.getEntity(entity: Entity): RecordableEntity? {
    return getEntityById(entity.entityId)
}

internal fun EntityMetaDataPacket.getMetadataArray(): ByteArray {
    return BinaryWriter().use { consumer?.accept(it); it.toByteArray() }
}

internal fun EquipmentHandler.getEquipmentForEntity(): Map<EntityEquipmentSlot, RecordableItemStack> =
    EntityEquipmentPacket.Slot.values().map {
        EntityEquipmentSlot.valueOf(it.name) to getEquipment(it).toReplay()
    }.toMap()

internal fun EquipmentHandler.setEquipmentForEntity(equipment: Map<EntityEquipmentSlot, RecordableItemStack>) =
    equipment.forEach {
        val item = it.value.toMinestom()
        when (it.key) {
            EntityEquipmentSlot.MAIN_HAND -> itemInMainHand = item
            EntityEquipmentSlot.OFF_HAND -> itemInOffHand = item
            EntityEquipmentSlot.BOOTS -> boots = item
            EntityEquipmentSlot.LEGGINGS -> leggings = item
            EntityEquipmentSlot.CHESTPLATE -> chestplate = item
            EntityEquipmentSlot.HELMET -> helmet = item
        }
    }