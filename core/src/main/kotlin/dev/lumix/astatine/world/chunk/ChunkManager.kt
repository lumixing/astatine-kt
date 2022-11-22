package dev.lumix.astatine.world.chunk

import com.badlogic.gdx.math.MathUtils
import dev.lumix.astatine.world.WorldGeneration
import dev.lumix.astatine.world.block.BlockType

class ChunkManager {
    companion object {
        const val chunksX = 16
        const val chunksY = 16
    }
    private val chunks: Array<Array<Chunk?>> = Array(chunksX) { Array(chunksY) { null } }
    private val loadedChunks: Array<Chunk?> = Array(36) { null }

    init {
        initChunks()
        WorldGeneration(this).generate()
    }

    private fun initChunks() {
        for (y in 0 until chunksY) {
            for (x in 0 until chunksX) {
                chunks[x][y] = Chunk(x, y)
            }
        }
    }

    fun render() {
        for (chunk in loadedChunks) {
            chunk?.render()
        }
    }

    // cx, cy are center chunk coords
    fun loadChunks(cx: Int, cy: Int) {
        if (loadedChunks[21] != null && loadedChunks[21]?.x == cx && loadedChunks[21]?.y == cy)
            return;
        var i = 0
        for (y in cy-3 until cy+3) {
            for (x in cx-3 until cx+3) {
                loadedChunks[i++] = getChunk(x, y)
            }
        }
    }

    private fun getChunk(x: Int, y: Int): Chunk? {
        if (!(x in 0 until chunksX && y in 0 until chunksY))
            return null;
        return chunks[x][y]
    }

    fun getBlock(x: Int, y: Int): BlockType? {
        val chunk = getChunk(MathUtils.floor((x / Chunk.chunkSize).toFloat()), MathUtils.floor((y / Chunk.chunkSize).toFloat()))
        return chunk?.getBlock(x % Chunk.chunkSize, y % Chunk.chunkSize);
    }

    fun setBlock(x: Int, y: Int, type: BlockType) {
        val chunk = getChunk(MathUtils.floor((x / Chunk.chunkSize).toFloat()), MathUtils.floor((y / Chunk.chunkSize).toFloat()))
        chunk?.setBlock(x % Chunk.chunkSize, y % Chunk.chunkSize, type);
    }
}