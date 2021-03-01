package io.github.openminigameserver.replay.player

import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.player.impl.*

object ActionPlayerManager {
    @PublishedApi
    internal val actionPlayers = mutableMapOf<Class<out RecordableAction>, ActionPlayer<*>>()

    init {
        registerActionPlayerGeneric(RecEntitySpawnPlayer)
        registerActionPlayerGeneric(RecEntityMovePlayer)
        registerActionPlayerGeneric(RecEntityMetadataPlayer)
        registerActionPlayerGeneric(RecPlayerHandAnimationPlayer)
        registerActionPlayerGeneric(RecEntitiesPositionPlayer)
        registerActionPlayerGeneric(RecEntityRemovePlayer)
        registerActionPlayerGeneric(RecBlockStateUpdatePlayer)
        registerActionPlayerGeneric(RecEntityEquipmentUpdatePlayer)
        registerActionPlayerGeneric(RecBlockStateBatchUpdatePlayer)
        registerActionPlayerGeneric(RecParticleEffectPlayer)
        registerActionPlayerGeneric(RecBlockEffectPlayer)
        registerActionPlayerGeneric(RecBlockBreakAnimationPlayer)
        registerActionPlayerGeneric(RecSoundEffectPlayer)
    }

    /**
     * Register an action player for the specified action type
     */
    @JvmStatic
    inline fun <reified R : RecordableAction, T : ActionPlayer<R>> registerActionPlayerGeneric(player: T) {
        actionPlayers[R::class.java] = player
    }

    /**
     * Register an action player for the specified action type
     */
    @JvmStatic
    fun <R : RecordableAction, T : ActionPlayer<R>> registerActionPlayer(playerInstance: T, recordableClazz: Class<R>) {
        actionPlayers[recordableClazz] = playerInstance
    }

    /**
     * Get an action player for the given action
     */
    @JvmStatic
    fun <R : RecordableAction> getActionPlayer(action: R): ActionPlayer<R> {
        @Suppress("UNCHECKED_CAST")
        return actionPlayers[action.javaClass] as? ActionPlayer<R>
            ?: throw Exception("Unable to find action player for ${action.javaClass.simpleName}")
    }
}