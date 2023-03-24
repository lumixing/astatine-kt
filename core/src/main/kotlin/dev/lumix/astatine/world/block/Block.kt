package dev.lumix.astatine.world.block

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import dev.lumix.astatine.engine.Static

class Block(val type: BlockType, val texture: AtlasRegion?, val wallTexture: AtlasRegion?, val sound: Sound?, val flip: Boolean) {
    companion object {
        const val BLOCK_SIZE = 8f
    }

    // absolute block position
    fun render(blockX: Int, blockY: Int, flipX: Boolean, flipY: Boolean) {
        val sprite = Sprite(texture)
        sprite.setPosition(blockX * BLOCK_SIZE, blockY * BLOCK_SIZE)
        if (flip) sprite.setFlip(flipX, flipY)
        sprite.draw(Static.batch)
    }

    fun renderWall(blockX: Int, blockY: Int, flipX: Boolean, flipY: Boolean) {
        val sprite = Sprite(wallTexture)
        sprite.setPosition(blockX * BLOCK_SIZE, blockY * BLOCK_SIZE)
        sprite.setFlip(flipX, flipY)
        sprite.draw(Static.batch)
    }
}