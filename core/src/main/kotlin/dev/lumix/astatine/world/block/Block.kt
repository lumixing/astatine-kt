package dev.lumix.astatine.world.block

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.math.MathUtils
import dev.lumix.astatine.engine.Static

class Block(val type: BlockType, private val texture: AtlasRegion?) {
    fun render(x: Int, y: Int) {
//        texture?.flip(MathUtils.randomBoolean(), MathUtils.randomBoolean())
        Static.batch.draw(texture, x * 8f, y * 8f)
    }
}