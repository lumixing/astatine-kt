package dev.lumix.astatine.world.chunk

import com.badlogic.gdx.math.MathUtils
import dev.lumix.astatine.world.block.BlockManager
import dev.lumix.astatine.world.block.BlockType

class Chunk(val x: Int, val y: Int) {
    companion object {
        const val CHUNK_SIZE = 32
    }

    private val blocks: Array<Array<BlockType?>> = Array(CHUNK_SIZE) { Array(CHUNK_SIZE) { null } }

//    init {
//        for (y in 0 until CHUNK_SIZE) {
//            for (x in 0 until CHUNK_SIZE) {
//                blocks[x][y] = if (MathUtils.randomBoolean()) BlockType.DIRT else BlockType.STONE
//            }
//        }
//    }

    fun render() {
        for (relativeBlockY in 0 until CHUNK_SIZE) {
            for (relativeBlockX in 0 until CHUNK_SIZE) {
                val block = blocks[relativeBlockX][relativeBlockY] ?: continue
                if (block == BlockType.AIR) continue

                val blockX = relativeBlockX + CHUNK_SIZE * this.x
                val blockY = relativeBlockY + CHUNK_SIZE * this.y
                BlockManager.getBlock(block)?.render(blockX, blockY)
            }
        }
    }

    fun getBlock(x: Int, y: Int): BlockType? {
        if (!(x in 0 until CHUNK_SIZE && y in 0 until CHUNK_SIZE))
            return null
        return blocks[x][y]
    }

    fun setBlock(x: Int, y: Int, type: BlockType) {
        if (!(x in 0 until CHUNK_SIZE && y in 0 until CHUNK_SIZE))
            return
        blocks[x][y] = type
    }
}