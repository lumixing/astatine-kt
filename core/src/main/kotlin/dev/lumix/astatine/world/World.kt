package dev.lumix.astatine.world

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector3
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
            position.set(200f, 4070f)
        }
        with<SpriteComponent>()
        with<PhysicsComponent>()
        with<PlayerComponent>()
    }
    val playerItem: Item<Entity> = physicsWorld.add(Item(playerEntity), 200f, 4070f, 8f, 8f)
    private val playerItemComponent = ItemComponent(playerItem)

    private val boxEntity = Static.engine.entity {
        with<TransformComponent> {
            position.set(210f, 4070f)
        }
        with<SpriteComponent>()
        with<PhysicsComponent>()
    }
    private val boxItem: Item<Entity> = physicsWorld.add(Item(boxEntity), 210f, 4070f, 8f, 8f)
    private val boxItemComponent = ItemComponent(boxItem)

    init {
        info { "adding item component" }
        playerEntity.add(playerItemComponent)
        boxEntity.add(boxItemComponent)
    }

    fun update() {
        val transform = playerEntity[TransformComponent.mapper]

        Static.camera.position.x = transform!!.position.x
        Static.camera.position.y = transform.position.y

        val centerUnprojPos = Static.camera.unproject(Vector3(1280f / 2f, 720f / 2f, 0f))
        val centerChunkPos = centerUnprojPos.cpy().scl(1/256f)
        chunkManager.loadChunks(centerChunkPos.x.toInt(), centerChunkPos.y.toInt())

        chunkManager.clearBlockEntities()
        chunkManager.updateBlockEntitiesNear((transform.position.x / 8).toInt(), (transform.position.y / 8).toInt())
    }

    fun render() {
        chunkManager.render()
    }
}