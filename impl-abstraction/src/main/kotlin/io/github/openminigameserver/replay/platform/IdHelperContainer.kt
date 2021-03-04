package io.github.openminigameserver.replay.platform

import java.util.concurrent.ConcurrentHashMap

class IdHelperContainer<K, W>(val compute: (K).() -> W) {

    private val values = ConcurrentHashMap<K, W>()

    fun getOrCompute(id: K): W {
        var result = values[id]
        if (result == null) {
            result = compute(id)
            values[id] = result
        }
        return result!!
    }
}