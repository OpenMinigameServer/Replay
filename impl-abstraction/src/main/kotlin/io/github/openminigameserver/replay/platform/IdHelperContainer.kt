package io.github.openminigameserver.replay.platform

class IdHelperContainer<K, W>(val compute: (K).() -> W) {

    private val values = mutableMapOf<K, W>()

    fun getOrCompute(id: K): W {
        return values.getOrPut(id) { compute(id) }
    }
}