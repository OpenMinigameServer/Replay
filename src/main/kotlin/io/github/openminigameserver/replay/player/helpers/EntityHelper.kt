package io.github.openminigameserver.replay.player.helpers

import io.github.openminigameserver.replay.model.recordable.entity.data.PlayerEntityData
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.LivingEntity
import net.minestom.server.utils.Position
import java.util.*

internal object EntityHelper {

    fun createEntity(type: EntityType, spawnPosition: Position, entityData: Any?): Entity =
        (if (type == EntityType.PLAYER && entityData is PlayerEntityData) {
            ReplayPlayerEntity(UUID.randomUUID(), entityData.userName).also {
                it.skin = entityData.skin
                it.settings
            }
        }
        else object : Entity(type, spawnPosition) {
            override fun update(time: Long) {
            }

            override fun spawn() {
            }

        }).also {
            it.isAutoViewable = false
            it.setNoGravity(true)
            if (it is LivingEntity) {
                it.health = it.maxHealth
            }
        }

}