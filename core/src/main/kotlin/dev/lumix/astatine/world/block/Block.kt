package dev.lumix.astatine.world.block

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import dev.lumix.astatine.engine.Static

class Block(val type: BlockType, private val texture: AtlasRegion?) {
    companion object {
        const val BLOCK_SIZE = 8f
    }

    fun render(x: Int, y: Int) {
        Static.batch.draw(texture, x * BLOCK_SIZE, y * BLOCK_SIZE)
    }
}