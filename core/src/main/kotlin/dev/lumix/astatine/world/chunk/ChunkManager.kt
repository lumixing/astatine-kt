package dev.lumix.astatine.world.chunk

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import com.dongbat.jbump.Item
import com.dongbat.jbump.World
import dev.lumix.astatine.ecs.components.BlockComponent
import dev.lumix.astatine.ecs.components.ItemComponent
import dev.lumix.astatine.ecs.components.SpriteComponent
import dev.lumix.astatine.ecs.components.TransformComponent
import dev.lumix.astatine.engine.Static
import dev.lumix.astatine.world.WorldGen
import dev.lumix.astatine.world.block.BlockType
import ktx.ashley.entity
import ktx.ashley.with

class ChunkManager(private val physicsWorld: World<Entity>) {
    companion object {
        const val CHUNKS_X = 16
        const val CHUNKS_Y = 16
    }

    private val chunks: Array<Array<Chunk?>> = Array(CHUNKS_X) { Array(CHUNKS_Y) { null } }
    private val blockEntities: com.badlogic.gdx.utils.Array<Entity> = com.badlogic.gdx.utils.Array()
    private val blockItems: com.badlogic.gdx.utils.Array<Item<Entity>> = com.badlogic.gdx.utils.Array()

    init {
        for (y in 0 until CHUNKS_Y) {
            for (x in 0 until CHUNKS_X) {
                chunks[x][y] = Chunk(x, y)
            }
        }

        WorldGen(this, 2L).generate()
    }

    fun render() {
        for (y in 0 until CHUNKS_Y) {
            for (x in 0 until CHUNKS_X) {
                chunks[x][y]?.render()
            }
        }
    }

    private fun getChunk(x: Int, y: Int): Chunk? {
        if (!(x in 0 until CHUNKS_X && y in 0 until CHUNKS_Y))
            return null;
        return chunks[x][y]
    }

    fun getBlock(x: Int, y: Int): BlockType? {
        val chunk = getChunk(MathUtils.floor((x / Chunk.CHUNK_SIZE).toFloat()), MathUtils.floor((y / Chunk.CHUNK_SIZE).toFloat()))
        return chunk?.getBlock(x % Chunk.CHUNK_SIZE, y % Chunk.CHUNK_SIZE);
    }

    fun setBlock(x: Int, y: Int, type: BlockType) {
        val chunk = getChunk(MathUtils.floor((x / Chunk.CHUNK_SIZE).toFloat()), MathUtils.floor((y / Chunk.CHUNK_SIZE).toFloat()))
        chunk?.setBlock(x % Chunk.CHUNK_SIZE, y % Chunk.CHUNK_SIZE, type);
    }

    fun updateBlockEntitiesNear(x: Int, y: Int) {
        for (j in y-3..y+3) {
            for (i in x-3..x+3) {
                if (getBlock(i, j) == BlockType.AIR) continue

                val blockEntity = Static.engine.entity {
                    with<TransformComponent> {
                        position.set(0f, 3800f)
                    }
                    with<SpriteComponent>()
                    with<BlockComponent>()
                }
                blockEntities.add(blockEntity)

                val blockItem: Item<Entity> = physicsWorld.add(Item(blockEntity), i*8f, j*8f, 8f, 8f)
                blockItems.add(blockItem)
                val itemComponent = ItemComponent(blockItem)
                blockEntity.add(itemComponent)
            }
        }
    }

    fun clearBlockEntities() {
        for (entity in blockEntities) {
            Static.engine.removeEntity(entity)
        }
        blockEntities.clear()

        for (item in blockItems) {
            physicsWorld.remove(item)
        }
        blockItems.clear()
    }
}