package io.github.openminigameserver.replay.io

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.smile.databind.SmileMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.github.luben.zstd.ZstdInputStream
import com.github.luben.zstd.ZstdOutputStream
import io.github.openminigameserver.replay.model.Replay
import java.io.File

class ReplayFile(private val file: File, var replay: Replay? = null, private val isCompressed: Boolean = true) {

    companion object {
        private val mapper = SmileMapper().apply {
            configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
            registerKotlinModule()
            registerModule(ReplayModule())
            registerModule(Jdk8Module())
            registerModule(JavaTimeModule())
        }

        fun doMapAttempt() {
            mapper.valueToTree<JsonNode>(Replay())
        }
    }

    fun loadReplay() {
        replay = mapper.treeToValue(mapper.readTree(getInputStream()))
    }

    fun saveReplay() {
        mapper.writeValue(getOutputStream(), replay)
    }

    private fun getInputStream() = if (isCompressed) ZstdInputStream(file.inputStream()) else file.inputStream()

    private fun getOutputStream() = if (isCompressed) ZstdOutputStream(file.outputStream()) else file.outputStream()

}