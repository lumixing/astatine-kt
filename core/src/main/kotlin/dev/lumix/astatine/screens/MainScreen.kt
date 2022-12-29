package dev.lumix.astatine.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import dev.lumix.astatine.ecs.systems.PhysicsSystem
import dev.lumix.astatine.ecs.systems.RenderSystem
import dev.lumix.astatine.engine.Static
import dev.lumix.astatine.world.World
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.graphics.use
import ktx.log.info

class MainScreen() : KtxScreen {
    private val world = World()
//    val profiler = GLProfiler(Gdx.graphics).apply {
//        enable()
//    }
    private val debugLeft = Array<String>()
    private val debugRight = Array<String>()

    override fun show() {
        super.show()
        Static.camera.zoom = .5f
        Static.camera.position.set(0f, 3800f, 0f)
        Static.engine.apply {
            addSystem(RenderSystem())
            addSystem(PhysicsSystem(world.world))
        }
        info { "systems: ${Static.engine.systems.size()}" }
        world.show()
    }

    override fun render(delta: Float) {
        // input
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit()
        if (Gdx.input.isKeyPressed(Input.Keys.W)) Static.camera.translate(0f, 10f)
        if (Gdx.input.isKeyPressed(Input.Keys.A)) Static.camera.translate(-10f, 0f)
        if (Gdx.input.isKeyPressed(Input.Keys.S)) Static.camera.translate(0f, -10f)
        if (Gdx.input.isKeyPressed(Input.Keys.D)) Static.camera.translate(10f, 0f)
        if (Gdx.input.isTouched) world.breakBlock(Static.camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)))

        // update
        Static.camera.update()
        Static.batch.projectionMatrix = Static.camera.combined
        world.update()
        updateDebug(delta)
//        profiler.reset()

        // render
        clearScreen(31/255f, 203/255f, 255/255f)
        Static.batch.use {
            world.render()
        }

        Static.debugBatch.use { batch ->
            debugLeft.forEachIndexed { i, str ->
                Static.font.draw(batch, str, 4f, 714f - (i * 18f))
            }
            debugRight.forEachIndexed { i, str ->
                val layout = GlyphLayout(Static.font, str)
                Static.font.draw(batch, layout, 1280f - layout.width - 4f, 720f - layout.height + 4 - (i * 18))
            }
        }

        world.debug.render(world.world, Static.camera.combined)
        Static.engine.update(delta)
    }

    private fun updateDebug(delta: Float) {
        val unprojMousePos = Static.camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
        val blockMousePos = unprojMousePos.cpy().scl(1/8f)
        val chunkMousePos = blockMousePos.cpy().scl(1/32f)
        val unprojCenterPos = Static.camera.unproject(Vector3(1280f / 2f, 720f / 2f, 0f))
        val chunkCenterPos = unprojCenterPos.cpy().scl(1/256f)

        debugLeft.clear()
        debugLeft.add("unproj mouse: (${unprojMousePos.x.toInt()}, ${unprojMousePos.y.toInt()})")
        debugLeft.add("block mouse: (${blockMousePos.x.toInt()}, ${blockMousePos.y.toInt()})")
        debugLeft.add("chunk mouse: (${chunkMousePos.x.toInt()}, ${chunkMousePos.y.toInt()})")
        debugLeft.add("chunk center: (${chunkCenterPos.x.toInt()}, ${chunkCenterPos.y.toInt()})")
        debugLeft.add("seed: ${world.chunkManager.seed}")
        debugLeft.add("engine entities pool: ${Static.engine.entities.size()}")
        debugLeft.add("world bodies: ${world.world.bodyCount}")
        debugLeft.add("block entities array: ${world.chunkManager.activeBlockEntities.size}")

        debugRight.clear()
        debugRight.add("fps: ${Gdx.graphics.framesPerSecond} (${MathUtils.floor(delta * 1000)}ms)")
//        debugRight.add("calls: ${profiler.calls}")
//        debugRight.add("draw calls: ${profiler.drawCalls}")
//        debugRight.add("texture bindings: ${profiler.textureBindings}")
    }
}