package dev.lumix.astatine.world.block

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import dev.lumix.astatine.engine.Static

class Block(val type: BlockType, val texture: AtlasRegion?) {
    companion object {
        const val BLOCK_SIZE = 8f
    }

    // absolute block position
    fun render(blockX: Int, blockY: Int) {
        Static.batch.draw(texture, blockX * BLOCK_SIZE, blockY * BLOCK_SIZE)
    }
}