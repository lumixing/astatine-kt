package dev.lumix.astatine.engine

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class Static {
    // why use injections haha..
    companion object {
        const val WIDTH = 1280
        const val HEIGHT = 720

        val assets = AssetManager()
        val batch = SpriteBatch().apply { enableBlending() }
        val debugBatch = SpriteBatch().apply { enableBlending() }
        val camera = OrthographicCamera().apply {
            setToOrtho(false, WIDTH.toFloat(), HEIGHT.toFloat())
        }
        val engine = PooledEngine()
        lateinit var font: BitmapFont
        var digRadius = 0
        var slot = 0
    }
}