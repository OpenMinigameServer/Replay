package io.github.openminigameserver.replay.replayer

import io.github.openminigameserver.replay.abstraction.ReplayEntity
import io.github.openminigameserver.replay.abstraction.ReplayUser
import io.github.openminigameserver.replay.abstraction.ReplayWorld
import io.github.openminigameserver.replay.model.recordable.RecordableAction
import io.github.openminigameserver.replay.platform.ReplayPlatform

open class ActionPlayerManager<W : ReplayWorld, P : ReplayUser, E : ReplayEntity>(val platform: ReplayPlatform<W, P, E>) {
    @PublishedApi
    internal val actionPlayers = mutableMapOf<Class<out RecordableAction>, ActionPlayer<*, W, P>>()

    /**
     * Register an action player for the specified action type
     */
    inline fun <reified R : RecordableAction, T : ActionPlayer<R, W, P>> registerActionPlayerGeneric(player: T) {
        actionPlayers[R::class.java] = player
    }

    /**
     * Register an action player for the specified action type
     */
    fun <R : RecordableAction, T : ActionPlayer<R, W, P>> registerActionPlayer(
        playerInstance: T,
        recordableClazz: Class<R>
    ) {
        actionPlayers[recordableClazz] = playerInstance
    }

    /**
     * Get an action player for the given action
     */
    fun <R : RecordableAction> getActionPlayer(action: R): ActionPlayer<R, W, P> {
        @Suppress("UNCHECKED_CAST")
        return actionPlayers[action.javaClass] as? ActionPlayer<R, W, P>
            ?: throw Exception("Unable to find action player for ${action.javaClass.simpleName}")
    }
}