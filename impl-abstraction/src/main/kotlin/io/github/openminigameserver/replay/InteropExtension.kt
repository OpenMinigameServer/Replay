package io.github.openminigameserver.replay

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

internal fun runOnSeparateThread(code: suspend CoroutineScope.() -> Unit) {
    thread {
        runBlocking(block = code)
    }
}
