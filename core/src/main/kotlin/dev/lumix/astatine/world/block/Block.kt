package dev.lumix.astatine.world.block

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import dev.lumix.astatine.Static

class Block(val type: BlockType, val texture: AtlasRegion?) {
    fun render(x: Int, y: Int) {
        Static.batch.draw(texture, x.toFloat() * 8, y.toFloat() * 8)
    }
}