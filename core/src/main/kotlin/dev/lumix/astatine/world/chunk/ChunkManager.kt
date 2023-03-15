package dev.lumix.astatine.world.chunk

import dev.lumix.astatine.world.WorldGen
import dev.lumix.astatine.world.block.BlockType
import ktx.log.debug

class ChunkManager {
    companion object {
        const val CHUNKS_X = 16
        const val CHUNKS_Y = 16
        const val CHUNK_RADIUS = 3
        const val LOADED_CHUNKS_SIZE = (CHUNK_RADIUS * 2) * (CHUNK_RADIUS * 2)
        const val SEED = 2L
    }

    // todo: use HashMap<Position, Chunk> instead of Array? (tried, not rly good, search other method!)
    private val chunks: Array<Array<Chunk?>> = Array(CHUNKS_X) { Array(CHUNKS_Y) { null } }
    private val loadedChunks: Array<Chunk?> = Array(LOADED_CHUNKS_SIZE) { null }

    init {
        initChunks()
        debug { "initialized ${CHUNKS_X * CHUNKS_Y} chunks" }
        WorldGen(this, SEED).generate()
    }

    private fun initChunks() {
        for (chunkY in 0 until CHUNKS_Y) {
            for (chunkX in 0 until CHUNKS_X) {
                chunks[chunkX][chunkY] = Chunk(chunkX, chunkY)
            }
        }
    }

    fun renderAllChunks() {
        for (chunkY in 0 until CHUNKS_Y) {
            for (chunkX in 0 until CHUNKS_X) {
                getChunk(chunkX, chunkY)?.render()
            }
        }
    }

    fun renderLoadedChunks() {
        for (chunk in loadedChunks) {
            chunk?.render()
        }
    }

    fun loadChunksNear(centerChunkX: Int, centerChunkY: Int) {
        // todo: hardcoded af, change it!
        // if center doesnt change then dont load new chunks
        if (loadedChunks[21] != null &&
            loadedChunks[21]?.chunkX == centerChunkX &&
            loadedChunks[21]?.chunkY == centerChunkY)
            return

        var i = 0
        for (chunkY in centerChunkY - CHUNK_RADIUS until centerChunkY + CHUNK_RADIUS) {
            for (chunkX in centerChunkX - CHUNK_RADIUS until centerChunkX + CHUNK_RADIUS) {
                loadedChunks[i++] = getChunk(chunkX, chunkY)
            }
        }
    }

    private fun getChunk(chunkX: Int, chunkY: Int): Chunk? {
        if (!isValidChunkPosition(chunkX, chunkY)) return null
        return chunks[chunkX][chunkY]
    }

    private fun isValidChunkPosition(chunkX: Int, chunkY: Int): Boolean {
        return chunkX in 0 until CHUNKS_X && chunkY in 0 until CHUNKS_Y
    }

    // todo: add Utils::absoluteToRelativeBlockPosition()
    fun getBlockType(blockX: Int, blockY: Int): BlockType? {
        val chunkX = blockX / Chunk.CHUNK_SIZE
        val chunkY = blockY / Chunk.CHUNK_SIZE
        val chunk = getChunk(chunkX, chunkY) ?: return null

        val relativeBlockX = blockX % Chunk.CHUNK_SIZE
        val relativeBlockY = blockY % Chunk.CHUNK_SIZE
        return chunk.getBlockType(relativeBlockX, relativeBlockY);
    }

    fun setBlockType(blockX: Int, blockY: Int, blockType: BlockType): Boolean {
        val chunkX = blockX / Chunk.CHUNK_SIZE
        val chunkY = blockY / Chunk.CHUNK_SIZE
        val chunk = getChunk(chunkX, chunkY) ?: return false

        val relativeBlockX = blockX % Chunk.CHUNK_SIZE
        val relativeBlockY = blockY % Chunk.CHUNK_SIZE
        return chunk.setBlockType(relativeBlockX, relativeBlockY, blockType);
    }
}