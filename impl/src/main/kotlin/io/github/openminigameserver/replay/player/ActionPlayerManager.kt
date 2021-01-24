package io.github.openminigameserver.replay.player

import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.player.impl.*

object ActionPlayerManager {

    private val actionPlayers = mutableMapOf<Class<out RecordableAction>, ActionPlayer<*>>()

    init {
        registerActionPlayer(RecEntitySpawnPlayer)
        registerActionPlayer(RecEntityMovePlayer)
        registerActionPlayer(RecEntityMetadataPlayer)
        registerActionPlayer(RecPlayerHandAnimationPlayer)
        registerActionPlayer(RecEntitiesPositionPlayer)
        registerActionPlayer(RecEntityRemovePlayer)
    }

    private inline fun <reified R : RecordableAction, T : ActionPlayer<R>> registerActionPlayer(rePlayer: T) {
        actionPlayers[R::class.java] = rePlayer
    }

    fun <R : RecordableAction> getActionPlayer(action: R): ActionPlayer<R> {
        @Suppress("UNCHECKED_CAST")
        return actionPlayers[action.javaClass] as? ActionPlayer<R>
            ?: throw Exception("Unable to find re-player for ${action.javaClass.simpleName}")
    }
}