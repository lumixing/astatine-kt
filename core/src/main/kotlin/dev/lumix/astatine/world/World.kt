package dev.lumix.astatine.world

import com.badlogic.ashley.core.Entity
import com.dongbat.jbump.Item
import com.dongbat.jbump.World
import dev.lumix.astatine.ecs.components.*
import dev.lumix.astatine.engine.Static
import dev.lumix.astatine.world.chunk.ChunkManager
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import ktx.log.info

class World {
    val physicsWorld = World<Entity>()
    private val chunkManager = ChunkManager(physicsWorld)
    val playerEntity = Static.engine.entity {
        with<TransformComponent> {
            position.set(200f, 4000f)
        }
        with<SpriteComponent>()
        with<PhysicsComponent>()
        with<PlayerComponent>()
    }
    val playerItem: Item<Entity> = physicsWorld.add(Item(playerEntity), 200f, 4000f, 8f, 8f)
    private val itemComponent = ItemComponent(playerItem)

    private val floorEntity = Static.engine.entity {
        with<TransformComponent> {
            position.set(0f, 3800f)
        }
        with<SpriteComponent>()
        with<BlockComponent>()
    }
    val floorItem: Item<Entity> = physicsWorld.add(Item(floorEntity), 0f, 3800f, 1000f, 8f)

    init {
        info { "adding item component" }
        playerEntity.add(itemComponent)

        chunkManager.updateBlockEntitiesNear(24, 478)
    }

    fun update() {
        val transform = playerEntity[TransformComponent.mapper]

        Static.camera.position.x = transform!!.position.x
        Static.camera.position.y = transform.position.y

        chunkManager.clearBlockEntities()
        chunkManager.updateBlockEntitiesNear((transform.position.x / 8).toInt(), (transform.position.y / 8).toInt())
    }

    fun render() {
        chunkManager.render()
    }
}