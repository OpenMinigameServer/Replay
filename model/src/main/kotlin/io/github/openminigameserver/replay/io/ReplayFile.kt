package io.github.openminigameserver.replay.io

import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.github.luben.zstd.ZstdInputStream
import com.github.luben.zstd.ZstdOutputStream
import io.github.openminigameserver.replay.io.format.readHeader
import io.github.openminigameserver.replay.io.format.readReplayData
import io.github.openminigameserver.replay.io.format.writeHeader
import io.github.openminigameserver.replay.io.format.writeReplayData
import io.github.openminigameserver.replay.model.Replay
import java.io.File

class ReplayFile(private val file: File, var replay: Replay? = null, private val isCompressed: Boolean = true) {

    fun loadReplay() {
        replay = Replay().apply {
            Input(getInputStream().readAllBytes()).use {
                it.readHeader(this)
                it.readReplayData(this)
            }
        }
    }

    fun saveReplay() {
        Output(getOutputStream()).use {
            it.writeHeader(replay!!)
            it.writeReplayData(replay!!)
        }
    }

    private fun getInputStream() = if (isCompressed) ZstdInputStream(file.inputStream()) else file.inputStream()

    private fun getOutputStream() = if (isCompressed) ZstdOutputStream(file.outputStream()) else file.outputStream()

}