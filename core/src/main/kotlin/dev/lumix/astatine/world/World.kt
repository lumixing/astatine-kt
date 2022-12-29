package dev.lumix.astatine.world

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.utils.Array
import dev.lumix.astatine.ecs.components.PhysicsComponent
import dev.lumix.astatine.ecs.components.PositionComponent
import dev.lumix.astatine.ecs.components.SpriteComponent
import dev.lumix.astatine.engine.Static
import dev.lumix.astatine.engine.TextureAtlasAssets
import dev.lumix.astatine.engine.get
import dev.lumix.astatine.world.block.BlockType
import dev.lumix.astatine.world.chunk.ChunkManager
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import ktx.box2d.createWorld
import ktx.log.info

class World {
    val world = createWorld(Vector2(0f, -130f))
    val chunkManager = ChunkManager(world)
    val debug = Box2DDebugRenderer()
    private val entities: Array<Entity> = Array()
    private val box = Static.engine.entity {
        with<PositionComponent> { position.set(120f, 3950f) }
        with<SpriteComponent> {
            sprite.setRegion(Static.assets[TextureAtlasAssets.Game].findRegion("lumix"))
            offset.set(-4f, -4f)
        }
        with<PhysicsComponent>()
    }

    fun show() {
        entities.add(box)
        for (i in 0..10) {
            for (j in 0..10) {
                val ent = Static.engine.entity {
                    with<PositionComponent> { position.set(120f + i * 10, 3950f + j * 10) }
                    with<SpriteComponent> {
                        sprite.setRegion(Static.assets[TextureAtlasAssets.Game].findRegion("stone"))
                        offset.set(-4f, -4f)
                    }
                    with<PhysicsComponent>()
                }
                entities.add(ent)
            }
        }
    }

    fun update() {
        world.step(1/60f, 6, 2)
        val centerUnprojPos = Static.camera.unproject(Vector3(1280f / 2f, 720f / 2f, 0f))
        val centerChunkPos = centerUnprojPos.cpy().scl(1/256f)
        chunkManager.loadChunks(centerChunkPos.x.toInt(), centerChunkPos.y.toInt())

        chunkManager.clearBlockEntities()
        entities.forEach {
            it[PositionComponent.mapper]?.let { position ->
                chunkManager.initBlockEntitiesNear((position.position.x / 8f).toInt(), (position.position.y / 8f).toInt())
            }
        }
    }

    fun breakBlock(unprojectedMouse: Vector3) {
        unprojectedMouse.scl(1/8f)
        info { "breaking block at (${unprojectedMouse.x}, ${unprojectedMouse.y})" }
        chunkManager.setBlock(unprojectedMouse.x.toInt(), unprojectedMouse.y.toInt(), BlockType.AIR)
    }

    fun render() {
        chunkManager.render()
    }
}