package dev.lumix.astatine.world.chunk

import com.badlogic.gdx.math.MathUtils
import dev.lumix.astatine.world.block.BlockManager
import dev.lumix.astatine.world.block.BlockType

class Chunk(val chunkX: Int, val chunkY: Int) {
    companion object {
        const val CHUNK_SIZE = 32
    }

    private val blocks: Array<Array<BlockType?>> = Array(CHUNK_SIZE) { Array(CHUNK_SIZE) { null } }
    private val walls: Array<Array<BlockType?>> = Array(CHUNK_SIZE) { Array(CHUNK_SIZE) { null } }
    private val flips: Array<Array<Pair<Boolean, Boolean>>> = Array(CHUNK_SIZE) { Array(CHUNK_SIZE) { Pair(true, true) } }

    init {
        // init flips
        for (i in 0 until CHUNK_SIZE) {
            for (j in 0 until CHUNK_SIZE) {
                flips[i][j] = Pair(MathUtils.randomBoolean(), MathUtils.randomBoolean())
            }
        }
    }

    fun render() {
        for (relativeBlockY in 0 until CHUNK_SIZE) {
            for (relativeBlockX in 0 until CHUNK_SIZE) {
                renderWall(relativeBlockX, relativeBlockY)

                // render blocks
                // dont render if block is null or air
                val blockType = getBlockType(relativeBlockX, relativeBlockY) ?: continue
                if (blockType == BlockType.AIR) continue

                // chunk relative to absolute block position
                val blockX = relativeBlockX + CHUNK_SIZE * chunkX
                val blockY = relativeBlockY + CHUNK_SIZE * chunkY
                val (flipX, flipY) = flips[relativeBlockX][relativeBlockY]

                BlockManager.getBlock(blockType)?.render(blockX, blockY, flipX, flipY)
            }
        }
    }

    private fun renderWall(relativeBlockX: Int, relativeBlockY: Int) {
        val wallBlockType = getWallBlockType(relativeBlockX, relativeBlockY) ?: return
        if (wallBlockType == BlockType.AIR) return

        // if block in front dont render wall
        val blockType = getBlockType(relativeBlockX, relativeBlockY)
        if (blockType != null && blockType != BlockType.AIR) return

        val blockX = relativeBlockX + CHUNK_SIZE * chunkX
        val blockY = relativeBlockY + CHUNK_SIZE * chunkY
        val (flipX, flipY) = flips[relativeBlockX][relativeBlockY]

        BlockManager.getBlock(wallBlockType)?.renderWall(blockX, blockY, flipX, flipY)
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

    // returns null if invalid block position
    fun getWallBlockType(relativeBlockX: Int, relativeBlockY: Int): BlockType? {
        if (!isValidBlockPosition(relativeBlockX, relativeBlockY)) return null
        return walls[relativeBlockX][relativeBlockY]
    }

    // returns true if succesfully sets a block
    fun setWallBlockType(relativeBlockX: Int, relativeBlockY: Int, blockType: BlockType): Boolean {
        if (!isValidBlockPosition(relativeBlockX, relativeBlockY)) return false
        walls[relativeBlockX][relativeBlockY] = blockType
        return true
    }

    private fun isValidBlockPosition(relativeBlockX: Int, relativeBlockY: Int): Boolean {
        return relativeBlockX in 0 until CHUNK_SIZE && relativeBlockY in 0 until CHUNK_SIZE
    }
}