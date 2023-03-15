package dev.lumix.astatine

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import dev.lumix.astatine.screens.LoadingScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen

class Astatine : KtxGame<KtxScreen>() {
    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG

        addScreen(LoadingScreen(this))
        setScreen<LoadingScreen>()
    }
}

