package io.github.openminigameserver.replay.replayer

class ReplayTicker(private val session: ReplaySession) : Runnable {
    override fun run() {
        session.tick()
    }

}
