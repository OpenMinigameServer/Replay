package io.github.openminigameserver.replay.player

import com.google.common.collect.HashBasedTable
import io.github.openminigameserver.replay.model.Replay
import io.github.openminigameserver.replay.model.recordable.RecordedChunk
import net.minestom.server.instance.*
import net.minestom.server.utils.binary.BinaryReader
import net.minestom.server.utils.chunk.ChunkCallback
import net.minestom.server.world.biomes.Biome
import java.util.*

class ReplayChunkLoader(val replay: Replay) : IChunkLoader {
    private val chunksMap: HashBasedTable<Int, Int, RecordedChunk> = HashBasedTable.create<Int, Int, RecordedChunk>()

    init {
        replay.chunks.forEach {
            chunksMap.put(it.chunkX, it.chunkZ, it)
        }
    }

    override fun loadChunk(instance: Instance, chunkX: Int, chunkZ: Int, callback: ChunkCallback?): Boolean {
        val biomes = arrayOfNulls<Biome>(Chunk.BIOME_COUNT).also { Arrays.fill(it, Biome.PLAINS) }
        val chunk = if (instance is InstanceContainer) instance.chunkSupplier.createChunk(
            biomes,
            chunkX,
            chunkZ
        ) else DynamicChunk(biomes, chunkX, chunkZ)

        val savedChunk = chunksMap.get(chunkX, chunkZ)
        if (savedChunk != null) {
            val reader = BinaryReader(savedChunk.data)
            chunk.readChunk(reader, callback)
        } else {
            callback?.accept(chunk)
        }
        return true

    }

    override fun saveChunk(chunk: Chunk, callback: Runnable?) {
        callback?.run()
    }
}