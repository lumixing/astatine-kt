package dev.lumix.astatine.world.chunk

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import com.dongbat.jbump.Item
import com.dongbat.jbump.World
import dev.lumix.astatine.ecs.components.BlockComponent
import dev.lumix.astatine.ecs.components.ItemComponent
import dev.lumix.astatine.ecs.components.TransformComponent
import dev.lumix.astatine.engine.Static
import dev.lumix.astatine.world.WorldGen
import dev.lumix.astatine.world.block.BlockType
import ktx.ashley.get
import ktx.assets.invoke
import ktx.assets.pool

class ChunkManager(private val physicsWorld: World<Entity>) {
    companion object {
        const val CHUNKS_X = 16
        const val CHUNKS_Y = 16
    }

    private val chunks: Array<Array<Chunk?>> = Array(CHUNKS_X) { Array(CHUNKS_Y) { null } }
    private val loadedChunks: Array<Chunk?> = Array(36) { null }
    private val blockEntities: com.badlogic.gdx.utils.Array<Entity> = com.badlogic.gdx.utils.Array()
    private val blockEntityPool = pool { Entity() }

    init {
        for (y in 0 until CHUNKS_Y) {
            for (x in 0 until CHUNKS_X) {
                chunks[x][y] = Chunk(x, y)
            }
        }

        WorldGen(this, 2L).generate()
    }

    fun render() {
        for (chunk in loadedChunks) {
            chunk?.render()
        }
    }

    // using chunk position
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
        if (!(x in 0 until CHUNKS_X && y in 0 until CHUNKS_Y))
            return null;
        return chunks[x][y]
    }

    fun getBlockType(x: Int, y: Int): BlockType? {
        val chunk = getChunk(MathUtils.floor((x / Chunk.CHUNK_SIZE).toFloat()), MathUtils.floor((y / Chunk.CHUNK_SIZE).toFloat()))
        return chunk?.getBlock(x % Chunk.CHUNK_SIZE, y % Chunk.CHUNK_SIZE);
    }

    fun setBlockType(x: Int, y: Int, type: BlockType) {
        val chunk = getChunk(MathUtils.floor((x / Chunk.CHUNK_SIZE).toFloat()), MathUtils.floor((y / Chunk.CHUNK_SIZE).toFloat()))
        chunk?.setBlock(x % Chunk.CHUNK_SIZE, y % Chunk.CHUNK_SIZE, type);
    }

    // todo: have a universal unit system instead of fighting with unproj & blocks
    fun updateBlockEntitiesNear(x: Int, y: Int) {
        for (j in y-3..y+3) {
            for (i in x-3..x+3) {
                if (getBlockType(i, j) == BlockType.AIR) continue

                val blockEntity = blockEntityPool().apply {
                    add(TransformComponent().apply { position.set(i*8f, j*8f) })
                    add(BlockComponent())
                }
                val blockItem = physicsWorld.add(Item(blockEntity), i*8f, j*8f, 8f, 8f)
                val blockItemComponent = ItemComponent(blockItem)
                blockEntity.add(blockItemComponent)

                blockEntities.add(blockEntity)
                Static.engine.addEntity(blockEntity)
            }
        }
    }

    fun clearBlockEntities() {
        for (entity in blockEntities) {
            physicsWorld.remove(entity[ItemComponent.mapper]!!.item)
            blockEntityPool(entity)
            Static.engine.removeEntity(entity)
        }
        blockEntities.clear()
    }
}