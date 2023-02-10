package dev.lumix.astatine.world.chunk

import dev.lumix.astatine.world.block.BlockManager
import dev.lumix.astatine.world.block.BlockType
import ktx.log.info

class Chunk(val chunkX: Int, val chunkY: Int) {
    companion object {
        const val CHUNK_SIZE = 32
    }

    private val blocks: Array<Array<BlockType?>> = Array(CHUNK_SIZE) { Array(CHUNK_SIZE) { null } }

    fun render() {
        for (relativeBlockY in 0 until CHUNK_SIZE) {
            for (relativeBlockX in 0 until CHUNK_SIZE) {
                val block = getBlockType(relativeBlockX, relativeBlockY) ?: continue
                if (block == BlockType.AIR) continue

                val blockX = relativeBlockX + CHUNK_SIZE * this.chunkX
                val blockY = relativeBlockY + CHUNK_SIZE * this.chunkY
                BlockManager.getBlock(block)?.render(blockX, blockY)
            }
        }
    }

    fun getBlockType(relativeBlockX: Int, relativeBlockY: Int): BlockType? {
        if (!isValidBlockPosition(relativeBlockX, relativeBlockY)) return null
        return blocks[relativeBlockX][relativeBlockY]
    }

    fun setBlockType(relativeBlockX: Int, relativeBlockY: Int, blockType: BlockType) {
        if (!isValidBlockPosition(relativeBlockX, relativeBlockY)) return
        blocks[relativeBlockX][relativeBlockY] = blockType
    }

    private fun isValidBlockPosition(relativeBlockX: Int, relativeBlockY: Int): Boolean {
        return relativeBlockX in 0 until CHUNK_SIZE && relativeBlockY in 0 until CHUNK_SIZE
    }
}