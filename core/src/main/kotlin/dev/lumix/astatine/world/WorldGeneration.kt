package dev.lumix.astatine.world

import com.badlogic.gdx.math.MathUtils
import dev.lumix.astatine.engine.SimplexNoise
import dev.lumix.astatine.world.block.BlockType
import dev.lumix.astatine.world.chunk.Chunk
import dev.lumix.astatine.world.chunk.ChunkManager


class WorldGeneration(private val chunkManager: ChunkManager, private val seed: Long = 0) {
    private val surfaceNoise = SimplexNoise().apply { genGrad(seed) }
    private val caveNoise = SimplexNoise().apply { genGrad(seed) }
    private val oresNoise = SimplexNoise().apply { genGrad(seed) }
    private val maxX = ChunkManager.chunksX * Chunk.chunkSize
    private val maxY = ChunkManager.chunksY * Chunk.chunkSize

    fun generate() {
        generateDirt();
        generateSurface();
        generateStone();
        generateCaves();
        generateOres();
    }

    private fun generateDirt() {
        for (y in 0 until maxY) {
            for (x in 0 until maxX) {
                chunkManager.setBlock(x, y, BlockType.DIRT)
            }
        }
    }

    private fun generateSurface() {
        val length = 24f
        val height = 8f
        val offset: Int = maxY - 30
        for (x in 0 until maxX) {
            val `val` = (surfaceNoise.generate(x / length, 0f) * height).toInt() + offset
            chunkManager.setBlock(x, `val`, BlockType.GRASS)
            for (y in `val` + 1 until maxY) {
                chunkManager.setBlock(x, y, BlockType.AIR)
            }
        }
    }

    private fun generateStone() {
        val length = 0.4f
        val height = 1.6f
        val offset: Int = maxY - 50
        for (x in 0 until maxX) {
            val `val` = (MathUtils.sin(x * length) * height + offset).toInt()
            for (y in `val` downTo 0) {
                if (y < `val` - 5) chunkManager.setBlock(x, y, BlockType.STONE) else chunkManager.setBlock(
                    x,
                    y,
                    if (MathUtils.randomBoolean()) BlockType.STONE else BlockType.DIRT
                )
            }
        }
    }

    private fun generateCaves() {
        val zoom = 10f
        val threshold = -0.2f // smaller number smaller caves
        for (y in 0 until maxY) {
            for (x in 0 until maxX) {
                if (chunkManager.getBlock(x, y) != BlockType.STONE) continue
                val `val` = caveNoise.generate(x / zoom, y / zoom)
                if (`val` < threshold) chunkManager.setBlock(x, y, BlockType.AIR)
            }
        }
    }

    private fun generateOres() {
        val zoom = 8f
        val threshold = -0.7f // smaller number smaller ores
        for (x in 0 until maxX) {
            for (y in 0 until maxY) {
                if (chunkManager.getBlock(x, y) != BlockType.STONE) continue
                val `val` = oresNoise.generate(x / zoom, y / zoom)
                if (`val` < threshold) chunkManager.setBlock(x, y, BlockType.ORE)
            }
        }
    }
}