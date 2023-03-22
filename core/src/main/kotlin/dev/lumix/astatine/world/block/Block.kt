package dev.lumix.astatine.world.block

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import dev.lumix.astatine.engine.Static

class Block(val type: BlockType, val texture: AtlasRegion?, val wallTexture: AtlasRegion?, val sound: Sound?) {
    companion object {
        const val BLOCK_SIZE = 8f
    }

    // absolute block position
    fun render(blockX: Int, blockY: Int) {
        Static.batch.draw(texture, blockX * BLOCK_SIZE, blockY * BLOCK_SIZE)
    }

    fun renderWall(blockX: Int, blockY: Int) {
        Static.batch.draw(wallTexture, blockX * BLOCK_SIZE, blockY * BLOCK_SIZE)
    }
}