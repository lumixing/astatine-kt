package dev.lumix.astatine.world.chunk

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import com.dongbat.jbump.Item
import com.dongbat.jbump.World
import dev.lumix.astatine.ecs.components.BlockComponent
import dev.lumix.astatine.ecs.components.ItemComponent
import dev.lumix.astatine.ecs.components.TransformComponent
import dev.lumix.astatine.engine.Static
import dev.lumix.astatine.engine.Utils
import dev.lumix.astatine.world.WorldGen
import dev.lumix.astatine.world.block.Block
import dev.lumix.astatine.world.block.BlockType
import ktx.ashley.get
import ktx.assets.invoke
import ktx.assets.pool
import ktx.log.info

class ChunkManager(private val physicsWorld: World<Entity>) {
    companion object {
        const val CHUNKS_X = 16
        const val CHUNKS_Y = 16
        const val CHUNK_RADIUS = 3
        const val LOADED_CHUNKS_SIZE = (CHUNK_RADIUS * 2) * (CHUNK_RADIUS * 2)
        const val SEED = 2L
    }

    private val chunks: Array<Array<Chunk?>> = Array(CHUNKS_X) { Array(CHUNKS_Y) { null } }
    private val loadedChunks: Array<Chunk?> = Array(LOADED_CHUNKS_SIZE) { null }
    private val blockEntities: com.badlogic.gdx.utils.Array<Entity> = com.badlogic.gdx.utils.Array()
    private val blockEntityPool = pool { Entity() }

    init {
        for (chunkY in 0 until CHUNKS_Y) {
            for (chunkX in 0 until CHUNKS_X) {
                chunks[chunkX][chunkY] = Chunk(chunkX, chunkY)
            }
        }

        WorldGen(this, SEED).generate()
    }

    fun renderLoadedChunks() {
        for (chunk in loadedChunks) {
            chunk?.render()
        }
    }

    fun loadChunksNear(centerChunkX: Int, centerChunkY: Int) {
        // if center doesnt change then dont load new chunks
        if (loadedChunks[21] != null &&
            loadedChunks[21]?.chunkX == centerChunkX &&
            loadedChunks[21]?.chunkY == centerChunkY)
            return;

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

    // todo: add blockToRelativeBlockPos()
    fun getBlockType(blockX: Int, blockY: Int): BlockType? {
        val chunkX = blockX / Chunk.CHUNK_SIZE
        val chunkY = blockY / Chunk.CHUNK_SIZE
        val chunk = getChunk(chunkX, chunkY) ?: return null

        val relativeBlockX = blockX % Chunk.CHUNK_SIZE
        val relativeBlockY = blockY % Chunk.CHUNK_SIZE
        return chunk.getBlockType(relativeBlockX, relativeBlockY);
    }

    fun setBlockType(blockX: Int, blockY: Int, blockType: BlockType) {
        val chunkX = blockX / Chunk.CHUNK_SIZE
        val chunkY = blockY / Chunk.CHUNK_SIZE
        val chunk = getChunk(chunkX, chunkY) ?: return

        val relativeBlockX = blockX % Chunk.CHUNK_SIZE
        val relativeBlockY = blockY % Chunk.CHUNK_SIZE
        chunk.setBlockType(relativeBlockX, relativeBlockY, blockType);
    }

    fun updateBlockEntitiesNear(blockX: Int, blockY: Int) {
        for (relativeBlockY in blockY - CHUNK_RADIUS..blockY + CHUNK_RADIUS) {
            for (relativeBlockX in blockX - CHUNK_RADIUS..blockX + CHUNK_RADIUS) {
                if (getBlockType(relativeBlockX, relativeBlockY) == BlockType.AIR) continue

                val unprojX = relativeBlockX * Block.BLOCK_SIZE
                val unprojY = relativeBlockY * Block.BLOCK_SIZE

                val blockEntity = blockEntityPool().apply {
                    add(TransformComponent().apply { position.set(unprojX, unprojY) })
                    add(BlockComponent())
                }

                val blockItem = physicsWorld.add(Item(blockEntity), unprojX,unprojY, Block.BLOCK_SIZE, Block.BLOCK_SIZE)
                val blockItemComponent = ItemComponent(blockItem)
                blockEntity.add(blockItemComponent)

                blockEntities.add(blockEntity)
                Static.engine.addEntity(blockEntity)
            }
        }
    }

    fun clearBlockEntities() {
        for (entity in blockEntities) {
            val itemComponent = entity[ItemComponent.mapper] ?: return Utils.expectComponent("block", "item")

            physicsWorld.remove(itemComponent.item)
            blockEntityPool(entity)
            Static.engine.removeEntity(entity)
        }
        blockEntities.clear()
    }
}