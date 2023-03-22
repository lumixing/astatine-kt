package dev.lumix.astatine.ecs.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Sprite
import com.dongbat.jbump.Item
import com.dongbat.jbump.World
import dev.lumix.astatine.ecs.components.*
import dev.lumix.astatine.engine.TextureAssets
import dev.lumix.astatine.engine.Static
import dev.lumix.astatine.engine.get

class Box(private val unprojX: Float, private val unprojY: Float) : Entity() {
    companion object {
        const val BOUNDS_X = 8f
        const val BOUNDS_Y = 8f
    }

    init {
        add(TransformComponent().apply { position.set(unprojX, unprojY) })
        add(SpriteComponent().apply {
            sprite = Sprite(Static.assets[TextureAssets.Yippert])
        })
        add(PhysicsComponent().apply { bounds.set(BOUNDS_X, BOUNDS_Y) })
        add(ExplosiveComponent())

        Static.engine.addEntity(this)
    }

    fun addItemToEntity(physicsWorld: World<Entity>) {
        val item: Item<Entity> = physicsWorld.add(Item(this), unprojX, unprojY, BOUNDS_X, BOUNDS_Y)
        add(ItemComponent(item))
    }

}