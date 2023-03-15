package dev.lumix.astatine.world.chunk

import dev.lumix.astatine.world.block.BlockManager
import dev.lumix.astatine.world.block.BlockType

class Chunk(val chunkX: Int, val chunkY: Int) {
    companion object {
        const val CHUNK_SIZE = 32
    }

    private val blocks: Array<Array<BlockType?>> = Array(CHUNK_SIZE) { Array(CHUNK_SIZE) { null } }

    fun render() {
        for (relativeBlockY in 0 until CHUNK_SIZE) {
            for (relativeBlockX in 0 until CHUNK_SIZE) {
                // dont render if block is null or air
                val blockType = getBlockType(relativeBlockX, relativeBlockY) ?: continue
                if (blockType == BlockType.AIR) continue

                // chunk relative to absolute block position
                val blockX = relativeBlockX + CHUNK_SIZE * chunkX
                val blockY = relativeBlockY + CHUNK_SIZE * chunkY
                BlockManager.getBlock(blockType)?.render(blockX, blockY)
            }
        }
    }

    // returns null if invalid block position
    fun getBlockType(relativeBlockX: Int, relativeBlockY: Int): BlockType? {
        if (!isValidBlockPosition(relativeBlockX, relativeBlockY)) return null
        return blocks[relativeBlockX][relativeBlockY]
    }

    // returns true if succesfully sets a block
    fun setBlockType(relativeBlockX: Int, relativeBlockY: Int, blockType: BlockType): Boolean {
        if (!isValidBlockPosition(relativeBlockX, relativeBlockY)) return false
        blocks[relativeBlockX][relativeBlockY] = blockType
        return true
    }

    private fun isValidBlockPosition(relativeBlockX: Int, relativeBlockY: Int): Boolean {
        return relativeBlockX in 0 until CHUNK_SIZE && relativeBlockY in 0 until CHUNK_SIZE
    }
}