package dev.lumix.astatine.world

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import dev.lumix.astatine.Static
import dev.lumix.astatine.world.chunk.ChunkManager

class World {
    private val chunkManager = ChunkManager()
//    private val world = createWorld(Vector2(0f, -10f))
//    private val debug = Box2DDebugRenderer()

    fun render() {
        chunkManager.render()
//        debug.render(world, Static.camera.combined)
    }

    fun update() {
//        world.step(1/60f, 6, 2)
        val centerUnprojPos = Static.camera.unproject(Vector3(1280f / 2f, 720f / 2f, 0f))
        val centerChunkPos = centerUnprojPos.cpy().scl(1/256f)
        chunkManager.loadChunks(centerChunkPos.x.toInt(), centerChunkPos.y.toInt())
    }
}