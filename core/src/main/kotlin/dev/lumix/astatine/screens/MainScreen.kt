package dev.lumix.astatine.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import dev.lumix.astatine.ecs.components.*
import dev.lumix.astatine.ecs.entities.Box
import dev.lumix.astatine.ecs.systems.PhysicsSystem
import dev.lumix.astatine.ecs.systems.PlayerSystem
import dev.lumix.astatine.ecs.systems.RenderSystem
import dev.lumix.astatine.engine.*
import dev.lumix.astatine.world.World
import dev.lumix.astatine.world.chunk.ChunkManager
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.ashley.get
import ktx.ashley.has
import ktx.graphics.use
import ktx.log.debug

class MainScreen : KtxScreen {
    private val world = World()
    private var selectionTexture = Static.assets[TextureAtlasAssets.Game].findRegion("selection")

    private val debugLeft = Array<String>()
    private val debugRight = Array<String>()
    private val shapeDrawer = ShapeRenderer()
    private var showEntityOutline = true
    private var showChunkOutline = false

    override fun show() {
        Static.camera.zoom = 0.5f
        Static.engine.apply {
            addSystem(RenderSystem())
            addSystem(PhysicsSystem(world.physicsWorld))
            addSystem(PlayerSystem(world))
        }
        debug { "added ${Static.engine.systems.size()} systems" }
    }

    override fun render(delta: Float) {
        // input
        handleInput(delta)

        // update
        Static.camera.update()
        Static.batch.projectionMatrix = Static.camera.combined
        world.update()
        updateDebug()

        // render
        clearScreen(31/255f, 203/255f, 255/255f)
        Static.batch.use {
            world.render()

            // render mouse selection sprite
            val unprojMousePos = Static.camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
            val blockMousePos = unprojMousePos.cpy().scl(1/8f)
            it.draw(selectionTexture, blockMousePos.x.toInt() * 8f, blockMousePos.y.toInt() * 8f)
        }

        renderDebugText()
        renderDebugShapes()

        Static.engine.update(delta)
    }

    private fun handleInput(delta: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit()
        }

        if (Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
            Static.camera.zoom -= 3f * delta
        }
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            Static.camera.zoom += 3f * delta
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_0)) {
            Static.camera.zoom = 0.5f
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT_BRACKET)) {
            Static.digRadius--
            if (Static.digRadius < 0) Static.digRadius = 0
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT_BRACKET)) {
            Static.digRadius++
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            showEntityOutline = !showEntityOutline
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
            showChunkOutline = !showChunkOutline
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            world.player[InventoryComponent.mapper]!!.inventory.clearInventory()
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F4)) {
            var i = 0
            for (entity in Static.engine.entities.iterator()) {
                if (!entity.has(PickableComponent.mapper)) continue
                world.physicsWorld.remove(entity[ItemComponent.mapper]!!.item)
                Static.engine.removeEntity(entity)
                i++
            }
            Static.assets[SoundAssets.Nuke].play(0.1f)
            debug { "removed $i pickable items" }
        }
        // todo: REMOVE THIS?
        if (Gdx.input.isKeyJustPressed(Input.Keys.F12)) {
            if (MathUtils.randomBoolean(0.05f)) {
                Static.assets[SoundAssets.Dick].play(0.1f)
            } else {
                Static.assets[SoundAssets.Yippee].play(0.1f, MathUtils.random(0.3f, 2f), 1f)
            }
            val unprojMousePos = Static.camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
            Box(unprojMousePos.x, unprojMousePos.y).apply {
                addItemToEntity(world.physicsWorld)
            }
        }
    }

    // todo: objectify this shit (maybe) research more efficient methods
    private fun updateDebug() {
        val unprojMousePos = Static.camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
        val blockMousePos = unprojMousePos.cpy().scl(1/8f)
        val chunkMousePos = blockMousePos.cpy().scl(1/32f)
        val playerTransform = world.player[TransformComponent.mapper] ?: return Utils.expectComponent("player", "transform")
        val playerPhysics = world.player[PhysicsComponent.mapper] ?: return Utils.expectComponent("player", "physics")
        val playerInventory = world.player[InventoryComponent.mapper] ?: return Utils.expectComponent("player", "inventory")
        val inventory = playerInventory.inventory
        val current = playerInventory.current

        debugLeft.clear()
        debugLeft.add("mouse: {${unprojMousePos.x.toInt()} ${unprojMousePos.y.toInt()}} (${blockMousePos.x.toInt()} ${blockMousePos.y.toInt()}) [${chunkMousePos.x.toInt()} ${chunkMousePos.y.toInt()}]")
        debugLeft.add("ent/items/blocks: ${Static.engine.entities.size()} / ${world.physicsWorld.countItems()} / ${world.blockEntityManager.getBlockEntityCount()}")
        debugLeft.add("pos/rect: (${playerTransform.position.x.toInt()} ${playerTransform.position.y.toInt()})")
        debugLeft.add("velocity: (${playerPhysics.velocity.x.toInt()} ${playerPhysics.velocity.y.toInt()})")
        debugLeft.add("inv0: ${inventory.array[0].item} (${inventory.array[0].amount})")
        debugLeft.add("inv1: ${inventory.array[1].item} (${inventory.array[1].amount})")
        debugLeft.add("inv2: ${inventory.array[2].item} (${inventory.array[2].amount})")
        debugLeft.add("inv3: ${inventory.array[3].item} (${inventory.array[3].amount})")
        debugLeft.add("inv4: ${inventory.array[4].item} (${inventory.array[4].amount})")
        debugLeft.add("current: $current")
        debugLeft.add("digradius: ${Static.digRadius}")

        debugRight.clear()
        debugRight.add("${Gdx.graphics.framesPerSecond} fps (${(Gdx.graphics.deltaTime * 1000).toInt()}ms)")
    }

    private fun renderDebugText() {
        Static.debugBatch.use { batch ->
            debugLeft.forEachIndexed { i, str ->
                Static.font.draw(batch, str, 4f, Static.HEIGHT - 6f - (i * 18f))
            }
            debugRight.forEachIndexed { i, str ->
                val layout = GlyphLayout(Static.font, str)
                Static.font.draw(batch, layout, Static.WIDTH - layout.width - 4f, Static.HEIGHT - layout.height + 4 - (i * 18))
            }
        }
    }

    private fun renderDebugShapes() {
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
                    shapeDrawer.line(x * 256f, 0f, x * 256f, ChunkManager.CHUNKS_Y * 256f)
                    shapeDrawer.line(0f, y * 256f, ChunkManager.CHUNKS_X * 256f, y * 256f)
                }
            }
        }

        shapeDrawer.end()
    }
}