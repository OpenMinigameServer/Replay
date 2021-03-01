package io.github.openminigameserver.replay.player

class ReplayTicker(private val session: ReplaySession) : Runnable {
    override fun run() {
        session.tick()
    }

}
