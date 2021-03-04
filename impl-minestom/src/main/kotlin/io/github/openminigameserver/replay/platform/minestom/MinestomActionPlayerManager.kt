package io.github.openminigameserver.replay.platform.minestom

import io.github.openminigameserver.replay.platform.ReplayPlatform
import io.github.openminigameserver.replay.replayer.ActionPlayerManager
import io.github.openminigameserver.replay.replayer.impl.*

class MinestomActionPlayerManager(platform: ReplayPlatform<MinestomReplayWorld, MinestomReplayUser, MinestomReplayEntity>) :
    ActionPlayerManager<MinestomReplayWorld, MinestomReplayUser, MinestomReplayEntity>(platform) {

    init {
        registerActionPlayerGeneric(RecBlockBreakAnimationPlayer)
        registerActionPlayerGeneric(RecBlockEffectPlayer)
        registerActionPlayerGeneric(RecBlockStateUpdatePlayer)
        registerActionPlayerGeneric(RecEntitiesPositionPlayer)
        registerActionPlayerGeneric(RecEntityEquipmentUpdatePlayer)
        registerActionPlayerGeneric(RecEntityMetadataPlayer)
        registerActionPlayerGeneric(RecEntityMovePlayer)
        registerActionPlayerGeneric(RecEntityRemovePlayer)
        registerActionPlayerGeneric(RecEntitySpawnPlayer)
        registerActionPlayerGeneric(RecParticleEffectPlayer)
        registerActionPlayerGeneric(RecPlayerHandAnimationPlayer)
        registerActionPlayerGeneric(RecSoundEffectPlayer)
    }
}