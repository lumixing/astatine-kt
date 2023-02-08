package dev.lumix.astatine.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import dev.lumix.astatine.ecs.components.PhysicsComponent
import dev.lumix.astatine.ecs.components.TransformComponent
import dev.lumix.astatine.ecs.systems.PhysicsSystem
import dev.lumix.astatine.ecs.systems.PlayerSystem
import dev.lumix.astatine.ecs.systems.RenderSystem
import dev.lumix.astatine.engine.Static
import dev.lumix.astatine.world.World
import dev.lumix.astatine.world.chunk.ChunkManager
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.ashley.get
import ktx.graphics.use

class MainScreen : KtxScreen {
//    val profiler = GLProfiler(Gdx.graphics).apply {
//        enable()
//    }
    private val world = World()
    private val debugLeft = Array<String>()
    private val debugRight = Array<String>()
    private val cameraSpeed = 500f
    private val shapeDrawer = ShapeRenderer()
    private var showEntityOutline = true
    private var showChunkOutline = false

    override fun show() {
        Static.engine.apply {
            addSystem(RenderSystem())
            addSystem(PhysicsSystem(world.physicsWorld))
            addSystem(PlayerSystem(world.physicsWorld))
        }
    }

    override fun render(delta: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit()

//        if (Gdx.input.isKeyPressed(Input.Keys.W)) Static.camera.translate(0f, cameraSpeed * delta * Static.camera.zoom)
//        if (Gdx.input.isKeyPressed(Input.Keys.A)) Static.camera.translate(-cameraSpeed * delta * Static.camera.zoom, 0f)
//        if (Gdx.input.isKeyPressed(Input.Keys.S)) Static.camera.translate(0f, -cameraSpeed * delta * Static.camera.zoom)
//        if (Gdx.input.isKeyPressed(Input.Keys.D)) Static.camera.translate(cameraSpeed * delta * Static.camera.zoom, 0f)

        if (Gdx.input.isKeyPressed(Input.Keys.EQUALS)) Static.camera.zoom -= 0.4f * delta
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) Static.camera.zoom += 0.4f * delta
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_0)) Static.camera.zoom = 0.5f

        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) showEntityOutline = !showEntityOutline
        if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) showChunkOutline = !showChunkOutline

        Static.camera.update()
        Static.batch.projectionMatrix = Static.camera.combined
        world.update()
        updateDebug()
//        profiler.reset()

        clearScreen(31/255f, 203/255f, 255/255f)
        Static.batch.use {
            world.render()
        }

        Static.debugBatch.use { batch ->
            debugLeft.forEachIndexed { i, str ->
                Static.font.draw(batch, str, 4f, Static.HEIGHT - 6f - (i * 18f))
            }
            debugRight.forEachIndexed { i, str ->
                val layout = GlyphLayout(Static.font, str)
                Static.font.draw(batch, layout, Static.WIDTH - layout.width - 4f, Static.HEIGHT - layout.height + 4 - (i * 18))
            }
        }

        shapeDrawer.projectionMatrix = Static.camera.combined
        shapeDrawer.begin(ShapeRenderer.ShapeType.Line)
        if (showEntityOutline) {
            shapeDrawer.color = Color.RED
            for (item in world.physicsWorld.rects.iterator()) {
                shapeDrawer.rect(item.x, item.y, item.w, item.h)
            }
        }

        if (showChunkOutline) {
            shapeDrawer.color = Color.GREEN
            for (x in 0..ChunkManager.CHUNKS_X) {
                for (y in 0..ChunkManager.CHUNKS_Y) {
                    shapeDrawer.line(x * 256f, 0f, x * 256f, ChunkManager.CHUNKS_X * 256f)
                    shapeDrawer.line(0f, y * 256f, ChunkManager.CHUNKS_Y * 256f, y * 256f)
                }
            }
        }
        shapeDrawer.end()

        Static.engine.update(delta)
    }

    private fun updateDebug() {
        val unprojMousePos = Static.camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
        val blockMousePos = unprojMousePos.cpy().scl(1/8f)
        val chunkMousePos = blockMousePos.cpy().scl(1/32f)
        val transform = world.playerEntity[TransformComponent.mapper]
        val rect = world.physicsWorld.getRect(world.playerItem)
        val physics = world.playerEntity[PhysicsComponent.mapper]

        debugLeft.clear()
        debugLeft.add("mouse: {${unprojMousePos.x.toInt()} ${unprojMousePos.y.toInt()}} (${blockMousePos.x.toInt()} ${blockMousePos.y.toInt()}) [${chunkMousePos.x.toInt()} ${chunkMousePos.y.toInt()}]")
        debugLeft.add("ent/items: ${Static.engine.entities.size()} / ${world.physicsWorld.countItems()}")
        debugLeft.add("pos/rect: (${transform!!.position.x.toInt()} ${transform.position.y.toInt()}) (${rect.x.toInt()} ${rect.y.toInt()})")
        debugLeft.add("velocity: (${physics!!.velocity.x.toInt()} ${physics.velocity.y.toInt()})")
        debugLeft.add("isGrounded: ${world.playerEntity[PhysicsComponent.mapper]!!.isGrounded} / ${world.playerItem.userData[PhysicsComponent.mapper]!!.isGrounded}")

        debugRight.clear()
        debugRight.add("${Gdx.graphics.framesPerSecond} fps (${(Gdx.graphics.deltaTime * 1000).toInt()}ms)")
//        debugRight.add("calls: ${profiler.calls}")
//        debugRight.add("draw calls: ${profiler.drawCalls}")
//        debugRight.add("texture bindings: ${profiler.textureBindings}")
    }
}