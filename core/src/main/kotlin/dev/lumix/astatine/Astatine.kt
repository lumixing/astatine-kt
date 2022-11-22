package dev.lumix.astatine

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import dev.lumix.astatine.screens.LoadingScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen

class Astatine : KtxGame<KtxScreen>() {
    override fun create() {
        addScreen(LoadingScreen(this))
        setScreen<LoadingScreen>()
        super.create()
    }
}

