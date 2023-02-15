package dev.lumix.astatine.world

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.dongbat.jbump.World
import dev.lumix.astatine.ecs.components.*
import dev.lumix.astatine.ecs.entities.Box
import dev.lumix.astatine.ecs.entities.Player
import dev.lumix.astatine.engine.Static
import dev.lumix.astatine.engine.Utils
import dev.lumix.astatine.world.block.Block
import dev.lumix.astatine.world.chunk.Chunk
import dev.lumix.astatine.world.chunk.ChunkManager
import ktx.ashley.get
import ktx.log.info

class World {
    val physicsWorld = World<Entity>()
    val chunkManager = ChunkManager(physicsWorld)

    val player = Player(200f, 3900f)
    private val box = Box(220f, 3900f)

    init {
        player.addItemToEntity(physicsWorld)
        box.addItemToEntity(physicsWorld)
    }

    fun update() {
        val transform = player[TransformComponent.mapper] ?: return Utils.expectComponent("player", "transform")

        val camPos = Static.camera.position.cpy()
        val finalPos = Vector3(transform.position.x, transform.position.y, 0f)
        Static.camera.position.set(camPos.lerp(finalPos, 0.2f))

        val centerUnprojPos = Static.camera.unproject(Vector3(Static.WIDTH / 2f, Static.HEIGHT / 2f, 0f))
        val centerChunkPos = centerUnprojPos.cpy().scl(1 / (Block.BLOCK_SIZE * Chunk.CHUNK_SIZE))
        chunkManager.loadChunksNear(centerChunkPos.x.toInt(), centerChunkPos.y.toInt())

        chunkManager.clearBlockEntities()
        val blockX = (transform.position.x / Block.BLOCK_SIZE).toInt()
        val blockY = (transform.position.y / Block.BLOCK_SIZE).toInt()
        chunkManager.updateBlockEntitiesNear(blockX, blockY)
    }

    fun render() {
        chunkManager.renderLoadedChunks()
    }
}