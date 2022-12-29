package dev.lumix.astatine.engine

import com.badlogic.ashley.core.PooledEngine
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
        val camera = OrthographicCamera().apply { setToOrtho(false, 1280f, 720f) }
        val engine = PooledEngine()
        lateinit var font: BitmapFont
    }
}