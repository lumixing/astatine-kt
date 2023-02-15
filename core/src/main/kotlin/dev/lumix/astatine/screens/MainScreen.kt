package dev.lumix.astatine.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import dev.lumix.astatine.ecs.components.InventoryComponent
import dev.lumix.astatine.ecs.components.PhysicsComponent
import dev.lumix.astatine.ecs.components.TransformComponent
import dev.lumix.astatine.ecs.entities.inventory.InventoryItem
import dev.lumix.astatine.ecs.systems.PhysicsSystem
import dev.lumix.astatine.ecs.systems.PlayerSystem
import dev.lumix.astatine.ecs.systems.RenderSystem
import dev.lumix.astatine.engine.*
import dev.lumix.astatine.world.World
import dev.lumix.astatine.world.block.BlockType
import dev.lumix.astatine.world.chunk.ChunkManager
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.ashley.get
import ktx.graphics.use

class MainScreen : KtxScreen {
    private val world = World()

    private var breakSound = Static.assets[SoundAssets.Break]
    private var selectionTexture = Static.assets[TextureAtlasAssets.Game].findRegion("selection")

    private val debugLeft = Array<String>()
    private val debugRight = Array<String>()
    private val shapeDrawer = ShapeRenderer()
    private var showEntityOutline = true
    private var showChunkOutline = false

    override fun show() {
        Static.camera.apply {
            zoom = 0.5f
            position.set(200f, 3800f, 0f)
        }

        Static.engine.apply {
            addSystem(RenderSystem())
            addSystem(PhysicsSystem(world.physicsWorld))
            addSystem(PlayerSystem(world.physicsWorld))
        }
    }

    override fun render(delta: Float) {
        handleInput(delta)

        Static.camera.update()
        Static.batch.projectionMatrix = Static.camera.combined
        world.update()
        updateDebug()

        clearScreen(31/255f, 203/255f, 255/255f)

        Static.batch.use {
            world.render()

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
            Static.camera.zoom -= 0.4f * delta
        }
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            Static.camera.zoom += 0.4f * delta
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_0)) {
            Static.camera.zoom = 0.5f
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

        if (Gdx.input.isTouched) {
            val blockPos = Static.camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)).scl(1/8f)
            val blockType = world.chunkManager.getBlockType(blockPos.x.toInt(), blockPos.y.toInt())

            // todo: fix this
            if (blockType != BlockType.AIR || blockType == null) {
                world.player[InventoryComponent.mapper]!!.inventory.addItem(InventoryItem(blockType, 1))
                world.chunkManager.setBlockType(blockPos.x.toInt(), blockPos.y.toInt(), BlockType.AIR)

                val soundId = breakSound.play(0.1f)
                breakSound.setPitch(soundId, MathUtils.random(0.7f, 1.1f))
            }
        }
    }

    private fun updateDebug() {
        val unprojMousePos = Static.camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
        val blockMousePos = unprojMousePos.cpy().scl(1/8f)
        val chunkMousePos = blockMousePos.cpy().scl(1/32f)
        val transform = world.player[TransformComponent.mapper] ?: return Utils.expectComponent("player", "transform")
//        val rect = world.physicsWorld.getRect(world.playerItem)
        val physics = world.player[PhysicsComponent.mapper] ?: return Utils.expectComponent("player", "physics")
        val inventory = world.player[InventoryComponent.mapper] ?: return Utils.expectComponent("player", "inventory")
        val inv = inventory.inventory
        val current = inventory.current

        debugLeft.clear()
        debugLeft.add("mouse: {${unprojMousePos.x.toInt()} ${unprojMousePos.y.toInt()}} (${blockMousePos.x.toInt()} ${blockMousePos.y.toInt()}) [${chunkMousePos.x.toInt()} ${chunkMousePos.y.toInt()}]")
        debugLeft.add("ent/items: ${Static.engine.entities.size()} / ${world.physicsWorld.countItems()}")
//        debugLeft.add("pos/rect: (${transform.position.x.toInt()} ${transform.position.y.toInt()}) (${rect.x.toInt()} ${rect.y.toInt()})")
        debugLeft.add("pos/rect: (${transform.position.x.toInt()} ${transform.position.y.toInt()})")
        debugLeft.add("velocity: (${physics.velocity.x.toInt()} ${physics.velocity.y.toInt()})")
//        debugLeft.add("isGrounded: ${world.player[PhysicsComponent.mapper].isGrounded} / ${world.playerItem.userData[PhysicsComponent.mapper].isGrounded}")
        debugLeft.add("inv0: ${inv.array[0].item} (${inv.array[0].amount})")
        debugLeft.add("inv1: ${inv.array[1].item} (${inv.array[1].amount})")
        debugLeft.add("inv2: ${inv.array[2].item} (${inv.array[2].amount})")
        debugLeft.add("inv3: ${inv.array[3].item} (${inv.array[3].amount})")
        debugLeft.add("inv4: ${inv.array[4].item} (${inv.array[4].amount})")
        debugLeft.add("current: $current")

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

//            shapeDrawer.color = Color.BLUE
//            val pos = world.player[TransformComponent.mapper].position
//            shapeDrawer.line(pos.x, pos.y-1f, pos.x+8f, pos.y-1f)
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
    }
}