@file:JvmName("Lwjgl3Launcher")

package dev.lumix.astatine.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import dev.lumix.astatine.Astatine
import dev.lumix.astatine.engine.Static

/** Launches the desktop (LWJGL3) application. */
fun main() {
    Lwjgl3Application(Astatine(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("astatine - rewritten! (pre-pre-pre-alpha)")
        setWindowedMode(Static.WIDTH, Static.HEIGHT)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
