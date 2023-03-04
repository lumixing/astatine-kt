package dev.lumix.astatine.world

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector3
import com.dongbat.jbump.World
import dev.lumix.astatine.ecs.components.BlockComponent
import dev.lumix.astatine.ecs.components.TransformComponent
import dev.lumix.astatine.ecs.entities.Box
import dev.lumix.astatine.ecs.entities.Item
import dev.lumix.astatine.ecs.entities.Player
import dev.lumix.astatine.engine.Static
import dev.lumix.astatine.engine.Utils
import dev.lumix.astatine.world.block.Block
import dev.lumix.astatine.world.block.BlockType
import dev.lumix.astatine.world.chunk.Chunk
import dev.lumix.astatine.world.chunk.ChunkManager
import ktx.ashley.get
import ktx.ashley.has

class World {
    val physicsWorld = World<Entity>()
    val chunkManager = ChunkManager(physicsWorld)

    val player = Player(200f, 3900f)
    private val box = Box(220f, 3900f)
    private val item = Item(180f, 4000f, BlockType.DEEPSLATE)

    init {
        player.addItemToEntity(physicsWorld)
        box.addItemToEntity(physicsWorld)
        item.addItemToEntity(physicsWorld)
    }

    fun update() {
        val playerTransform = player[TransformComponent.mapper] ?: return Utils.expectComponent("player", "transform")

        // enable for camera peeking
//        val mouseX = Gdx.input.x - Static.WIDTH / 2
//        val mouseY = (Gdx.input.y - Static.HEIGHT / 2)*-1
        val cameraPosition = Static.camera.position.cpy()
        val finalCameraPosition = Vector3(playerTransform.position.x, playerTransform.position.y, 0f)
//        val finalPos = Vector3(transform.position.x + mouseX/5, transform.position.y + mouseY/5, 0f)
        Static.camera.position.set(cameraPosition.lerp(finalCameraPosition, 0.2f))

        val centerChunkPosition = playerTransform.position.cpy().scl(1 / (Block.BLOCK_SIZE * Chunk.CHUNK_SIZE))
        chunkManager.loadChunksNear(centerChunkPosition.x.toInt(), centerChunkPosition.y.toInt())

        chunkManager.clearBlockEntities()

        for (entity in Static.engine.entities.iterator()) {
            if (entity.has(BlockComponent.mapper)) continue

            val entityTransform = entity[TransformComponent.mapper] ?: return Utils.expectComponent("entity", "transform")
            val blockX = (entityTransform.position.x / Block.BLOCK_SIZE).toInt()
            val blockY = (entityTransform.position.y / Block.BLOCK_SIZE).toInt()
            chunkManager.updateBlockEntitiesNear(blockX, blockY)
        }
    }

    fun render() {
        chunkManager.renderLoadedChunks()
    }
}