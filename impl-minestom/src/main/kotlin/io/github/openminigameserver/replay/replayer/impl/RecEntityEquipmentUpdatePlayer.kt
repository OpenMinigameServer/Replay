package io.github.openminigameserver.replay.replayer.impl

import io.github.openminigameserver.replay.extensions.setEquipmentForEntity
import io.github.openminigameserver.replay.model.recordable.entity.RecordableEntity
import io.github.openminigameserver.replay.model.recordable.impl.RecEntityEquipmentUpdate
import io.github.openminigameserver.replay.platform.minestom.replayer.MinestomEntityActionPlayer
import io.github.openminigameserver.replay.replayer.ReplaySession
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.inventory.EquipmentHandler

object RecEntityEquipmentUpdatePlayer : MinestomEntityActionPlayer<RecEntityEquipmentUpdate>() {
    override fun play(
        action: RecEntityEquipmentUpdate,
        replayEntity: RecordableEntity,
        nativeEntity: Entity,
        session: ReplaySession,
        instance: Instance,
        viewers: List<Player>
    ) {
        if (nativeEntity is EquipmentHandler) {
            nativeEntity.setEquipmentForEntity(action.equipment)
        }
    }
}