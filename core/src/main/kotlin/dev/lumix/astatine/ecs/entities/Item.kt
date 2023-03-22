package dev.lumix.astatine.ecs.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Sprite
import com.dongbat.jbump.Item
import com.dongbat.jbump.World
import dev.lumix.astatine.ecs.components.*
import dev.lumix.astatine.engine.Static
import dev.lumix.astatine.engine.TextureAtlasAssets
import dev.lumix.astatine.engine.get
import dev.lumix.astatine.world.block.BlockManager
import dev.lumix.astatine.world.block.BlockType

class Item(private val unprojX: Float, private val unprojY: Float, val block: BlockType) : Entity() {
    companion object {
        const val BOUNDS_X = 4f
        const val BOUNDS_Y = 4f
    }

    init {
        add(TransformComponent().apply {
            position.set(unprojX, unprojY)
            scale.set(0.5f, 0.5f)
        })
        add(SpriteComponent().apply {
            sprite = Sprite(BlockManager.getBlock(block)?.texture)
            offset.set(-2f, -2f)
        })
        add(PhysicsComponent().apply { bounds.set(BOUNDS_X, BOUNDS_Y) })
        add(PickableComponent())

        Static.engine.addEntity(this)
    }

    fun addItemToEntity(physicsWorld: World<Entity>) {
        val item: Item<Entity> = physicsWorld.add(Item(this), unprojX, unprojY, BOUNDS_X, BOUNDS_Y)
        add(ItemComponent(item))
    }

}