@file:JvmName("Lwjgl3Launcher")

package dev.lumix.astatine.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import dev.lumix.astatine.Astatine
import dev.lumix.astatine.engine.Static

/** Launches the desktop (LWJGL3) application. */
fun main() {
    Lwjgl3Application(Astatine(), Lwjgl3ApplicationConfiguration().apply {
        val splashTexts = arrayOf(
            "now in kotlin!",
            "i lost count..",
            "by lumix!",
            "currently radioactive",
            "VERY in development",
            "open source",
            "mfbot would be proud",
            "has *some* bugs",
            "without log4j, i think",
            "with chunk culling optimizations",
            "with block entities overlap optimization",
            "null",
            "undefined",
            "ArrayIndexOutOfBoundsException",
            "*insert splash text here*",
            "made with some love",
            "now with splash texts!",
            "im in a window title!",
            "view is cool from up here"
        )

        setTitle("astatine v0.00∞ - ${splashTexts.random()}")
        setWindowedMode(Static.WIDTH, Static.HEIGHT)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
