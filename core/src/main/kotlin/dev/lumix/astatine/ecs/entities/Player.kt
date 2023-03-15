package dev.lumix.astatine.ecs.entities

import com.badlogic.ashley.core.Entity
import com.dongbat.jbump.Item
import com.dongbat.jbump.World
import dev.lumix.astatine.ecs.components.*
import dev.lumix.astatine.engine.Static

class Player(private val unprojX: Float, private val unprojY: Float) : Entity() {
    companion object {
        const val BOUNDS_X = 8f
        const val BOUNDS_Y = 8f
    }

    init {
        add(TransformComponent().apply { position.set(unprojX, unprojY) })
        add(SpriteComponent())
        add(PhysicsComponent().apply { bounds.set(BOUNDS_X, BOUNDS_Y) })
        add(PlayerComponent())
        add(InventoryComponent())

        Static.engine.addEntity(this)
    }

    fun addItemToEntity(physicsWorld: World<Entity>) {
        val item: Item<Entity> = physicsWorld.add(Item(this), unprojX, unprojY, BOUNDS_X, BOUNDS_Y)
        add(ItemComponent(item))
    }

}