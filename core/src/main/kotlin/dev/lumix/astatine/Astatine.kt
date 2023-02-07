package dev.lumix.astatine

import dev.lumix.astatine.screens.LoadingScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen

class Astatine : KtxGame<KtxScreen>() {
    override fun create() {
        addScreen(LoadingScreen(this))
        setScreen<LoadingScreen>()
    }
}

