package dev.lumix.astatine.world.chunk

import com.badlogic.ashley.core.Entity
import dev.lumix.astatine.world.block.BlockManager
import dev.lumix.astatine.world.block.BlockType

class Chunk(val x: Int, val y: Int) {
    companion object {
        const val chunkSize = 32
    }
    private val blocks: Array<Array<BlockType?>> = Array(chunkSize) { Array(chunkSize) { null } }

    fun render() {
        for (y in 0 until chunkSize) {
            for (x in 0 until chunkSize) {
                val block = blocks[x][y] ?: continue
                if (block == BlockType.AIR) continue

                val rx = x + chunkSize * this.x
                val ry = y + chunkSize * this.y
                BlockManager.getBlock(block)?.render(rx, ry)
            }
        }
    }

    fun getBlock(x: Int, y: Int): BlockType? {
        if (!(x in 0 until chunkSize && y in 0 until chunkSize))
            return null
        return blocks[x][y]
    }

    fun setBlock(x: Int, y: Int, type: BlockType) {
        if (!(x in 0 until chunkSize && y in 0 until chunkSize))
            return
        blocks[x][y] = type
    }
}