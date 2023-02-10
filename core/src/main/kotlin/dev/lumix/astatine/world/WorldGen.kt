package dev.lumix.astatine.world

import com.badlogic.gdx.math.MathUtils
import dev.lumix.astatine.engine.SimplexNoise
import dev.lumix.astatine.world.block.BlockType
import dev.lumix.astatine.world.chunk.Chunk
import dev.lumix.astatine.world.chunk.ChunkManager

class WorldGen(private val chunkManager: ChunkManager, private val seed: Long = 0) {
    companion object {
        const val MAX_BLOCK_X = ChunkManager.CHUNKS_X * Chunk.CHUNK_SIZE
        const val MAX_BLOCK_Y = ChunkManager.CHUNKS_Y * Chunk.CHUNK_SIZE

        const val SURFACE_LENGTH = 24F
        const val SURFACE_HEIGHT = 8F
        const val SURFACE_OFFSET = MAX_BLOCK_Y - 30

        const val STONE_LENGTH = 0.4f
        const val STONE_HEIGHT = 1.6f
        const val STONE_OFFSET = MAX_BLOCK_Y - 50
        const val STONE_THRESHOLD = 5

        const val CAVES_SCALE = 10f
        const val CAVES_TRESHOLD = -0.2f

        const val ORES_SCALE = 8f
        const val ORES_TRESHOLD = -0.7f
    }

    private val surfaceNoise = SimplexNoise().apply { genGrad(seed) }
    private val caveNoise = SimplexNoise().apply { genGrad(seed) }
    private val oresNoise = SimplexNoise().apply { genGrad(seed) }

    fun generate() {
        generateDirt();
        generateSurface();
        generateStone();
        generateCaves();
        generateOres();
    }

    private fun generateDirt() {
        for (blockY in 0 until MAX_BLOCK_Y) {
            for (blockX in 0 until MAX_BLOCK_X) {
                chunkManager.setBlockType(blockX, blockY, BlockType.DIRT)
            }
        }
    }

    private fun generateSurface() {
        for (blockX in 0 until MAX_BLOCK_X) {
            val value = (surfaceNoise.generate(blockX / SURFACE_LENGTH, 0f) * SURFACE_HEIGHT).toInt() + SURFACE_OFFSET

            chunkManager.setBlockType(blockX, value, BlockType.GRASS)
            for (blockY in value + 1 until MAX_BLOCK_Y) {
                chunkManager.setBlockType(blockX, blockY, BlockType.AIR)
            }
        }
    }

    private fun generateStone() {
        for (blockX in 0 until MAX_BLOCK_X) {
            val value = (MathUtils.sin(blockX * STONE_LENGTH) * STONE_HEIGHT + STONE_OFFSET).toInt()

            for (blockY in value downTo 0) {
                if (blockY < value - STONE_THRESHOLD) {
                    chunkManager.setBlockType(blockX, blockY, BlockType.STONE)
                    continue
                }

                val blockType = if (MathUtils.randomBoolean()) BlockType.STONE else BlockType.DIRT
                chunkManager.setBlockType(blockX, blockY, blockType)
            }
        }
    }

    private fun generateCaves() {
        for (blockY in 0 until MAX_BLOCK_Y) {
            for (blockX in 0 until MAX_BLOCK_X) {
                if (chunkManager.getBlockType(blockX, blockY) != BlockType.STONE) continue

                val value = caveNoise.generate(blockX / CAVES_SCALE, blockY / CAVES_SCALE)
                if (value < CAVES_TRESHOLD) {
                    chunkManager.setBlockType(blockX, blockY, BlockType.AIR)
                }
            }
        }
    }

    private fun generateOres() {
        for (blockY in 0 until MAX_BLOCK_Y) {
            for (blockX in 0 until MAX_BLOCK_X) {
                if (chunkManager.getBlockType(blockX, blockY) != BlockType.STONE) continue

                val value = oresNoise.generate(blockX / ORES_SCALE, blockY / ORES_SCALE)
                if (value < ORES_TRESHOLD) {
                    chunkManager.setBlockType(blockX, blockY, BlockType.ORE)
                }
            }
        }
    }
}