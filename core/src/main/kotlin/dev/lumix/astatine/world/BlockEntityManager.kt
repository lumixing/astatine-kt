package dev.lumix.astatine.world

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Array
import com.dongbat.jbump.Item
import com.dongbat.jbump.World
import dev.lumix.astatine.ecs.components.BlockComponent
import dev.lumix.astatine.ecs.components.ItemComponent
import dev.lumix.astatine.ecs.components.TransformComponent
import dev.lumix.astatine.engine.Static
import dev.lumix.astatine.engine.Utils
import dev.lumix.astatine.world.block.Block
import dev.lumix.astatine.world.block.BlockType
import dev.lumix.astatine.world.chunk.ChunkManager
import ktx.ashley.get
import ktx.assets.invoke
import ktx.assets.pool

class BlockEntityManager(private val physicsWorld: World<Entity>, private val chunkManager: ChunkManager) {
    companion object {
        const val BLOCK_ENTITY_RADIUS = 2
    }

    private val blockEntities: Array<Entity> = Array()
    private val blockEntityPool = pool { Entity() }
    private val blockEntityPositions: Array<Pair<Int, Int>> = Array()

    // todo: split to BlockEntityManager?
    fun updateBlockEntitiesNear(blockX: Int, blockY: Int) {
        for (relativeBlockY in blockY - BLOCK_ENTITY_RADIUS..blockY + BLOCK_ENTITY_RADIUS) {
            for (relativeBlockX in blockX - BLOCK_ENTITY_RADIUS..blockX + BLOCK_ENTITY_RADIUS) {
                // ignore overlapping block entities
                val relativeBlockPosition = Pair(relativeBlockX, relativeBlockY)
                if (blockEntityPositions.contains(relativeBlockPosition)) continue
                blockEntityPositions.add(relativeBlockPosition)

                if (chunkManager.getBlockType(relativeBlockX, relativeBlockY) == BlockType.AIR) continue

                val unprojX = relativeBlockX * Block.BLOCK_SIZE
                val unprojY = relativeBlockY * Block.BLOCK_SIZE

                // todo: use ecs/entities/BlockEntity
                val blockEntity = blockEntityPool().apply {
                    add(TransformComponent().apply { position.set(unprojX, unprojY) })
                    add(BlockComponent())
                }

                val blockItem = physicsWorld.add(Item(blockEntity), unprojX, unprojY, Block.BLOCK_SIZE, Block.BLOCK_SIZE)
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
        blockEntityPositions.clear()
    }

    fun getBlockEntityCount(): Int {
        return blockEntities.size
    }
}