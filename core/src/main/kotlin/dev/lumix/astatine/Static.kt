package dev.lumix.astatine

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class Static {
    companion object {
        val assets = AssetManager()
        val batch = SpriteBatch().apply {
            enableBlending()
        }
        val debugBatch = SpriteBatch()
//        val font = BitmapFont()
        val camera = OrthographicCamera().apply { setToOrtho(false, 1280f, 720f) }
        lateinit var font: BitmapFont
    }
}