package dev.lumix.astatine.world.chunk

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import dev.lumix.astatine.ecs.components.PhysicsComponent
import dev.lumix.astatine.ecs.components.PositionComponent
import dev.lumix.astatine.engine.Static
import dev.lumix.astatine.world.WorldGeneration
import dev.lumix.astatine.world.block.BlockType
import ktx.ashley.get
import ktx.assets.invoke
import ktx.assets.pool

class ChunkManager(private val world: World) {
    companion object {
        const val chunksX = 16
        const val chunksY = 16
    }
    private val chunks: Array<Array<Chunk?>> = Array(chunksX) { Array(chunksY) { null } }
    private val loadedChunks: Array<Chunk?> = Array(36) { null }
    val activeBlockEntities: com.badlogic.gdx.utils.Array<Entity> = com.badlogic.gdx.utils.Array()
    private val blockEntitiesPool = pool { Entity() }
    val seed = MathUtils.random(99999999L)

    init {
        initChunks()
        WorldGeneration(this, seed).generate()
    }

    private fun initChunks() {
        for (y in 0 until chunksY) {
            for (x in 0 until chunksX) {
                chunks[x][y] = Chunk(x, y)
            }
        }
    }

    fun render() {
        for (chunk in loadedChunks) {
            chunk?.render()
        }
    }

    // cx, cy are center chunk coords
    fun loadChunks(cx: Int, cy: Int) {
        if (loadedChunks[21] != null && loadedChunks[21]?.x == cx && loadedChunks[21]?.y == cy)
            return;
        var i = 0
        for (y in cy-3 until cy+3) {
            for (x in cx-3 until cx+3) {
                loadedChunks[i++] = getChunk(x, y)
            }
        }
    }

    private fun getChunk(x: Int, y: Int): Chunk? {
        if (!(x in 0 until chunksX && y in 0 until chunksY))
            return null;
        return chunks[x][y]
    }

    fun getBlock(x: Int, y: Int): BlockType? {
        val chunk = getChunk(MathUtils.floor((x / Chunk.chunkSize).toFloat()), MathUtils.floor((y / Chunk.chunkSize).toFloat()))
        return chunk?.getBlock(x % Chunk.chunkSize, y % Chunk.chunkSize);
    }

    fun setBlock(x: Int, y: Int, type: BlockType) {
        val chunk = getChunk(MathUtils.floor((x / Chunk.chunkSize).toFloat()), MathUtils.floor((y / Chunk.chunkSize).toFloat()))
        chunk?.setBlock(x % Chunk.chunkSize, y % Chunk.chunkSize, type);
    }

    fun initBlockEntitiesNear(x: Int, y: Int) {
        for (j in y-3 until y+3) {
            for (i in x-3 until x+3) {
                // overlapping block entities optimization
                var already = false
                for (it in activeBlockEntities) {
                    // wtf is this ye dont ask best way to do inline
                    if ((it[PositionComponent.mapper]?.position?.x?.div(8f))?.toInt() == i &&
                        (it[PositionComponent.mapper]?.position?.y?.div(8f))?.toInt() == j) {
                        already = true
                        break
                    }
                }
                if (already) continue

                if (getBlock(i, j) == BlockType.AIR) continue
                val px = i * 8f
                val py = j * 8f

                val ent = blockEntitiesPool()

                val positionComponent = PositionComponent().apply { position.set(px, py) }
                val physicsComponent = PhysicsComponent().apply { type = BodyDef.BodyType.StaticBody }
//                val spriteComponent = SpriteComponent().apply { sprite.setRegion(Static.assets[TextureAtlasAssets.Game].findRegion("lumix")) }

                ent.add(positionComponent)
                ent.add(physicsComponent)
//                ent.add(spriteComponent)

                activeBlockEntities.add(ent)
                Static.engine.addEntity(ent)
            }
        }
    }

    fun clearBlockEntities() {
        activeBlockEntities.forEach { entity ->
            entity[PhysicsComponent.mapper]?.let { physics ->
                world.destroyBody(physics.body)
            }
            blockEntitiesPool(entity)
            Static.engine.removeEntity(entity)
        }
        activeBlockEntities.clear()
    }
}
