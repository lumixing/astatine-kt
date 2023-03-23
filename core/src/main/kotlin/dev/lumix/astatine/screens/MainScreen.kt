package dev.lumix.astatine.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.MenuBar
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.VisLabel
import dev.lumix.astatine.engine.Static
import ktx.actors.onClick
import ktx.actors.stage
import ktx.app.KtxInputAdapter
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.scene2d.*
import ktx.scene2d.vis.*

class MainScreen : KtxScreen, KtxInputAdapter {
    val stage = stage(batch = Static.batch)

    override fun show() {
        VisUI.load()
        Scene2DSkin.defaultSkin = VisUI.getSkin()

        var button: Actor

        stage.actors {
            table {
                button = label("astatine")
                setFillParent(true)
            }
        }

        // fuck this
        val action = Actions.sequence(
            Actions.scaleBy(50f, 50f, 0.5f),
            Actions.scaleBy(-50f, -50f, 0.5f)
        )
        button.addAction(Actions.repeat(RepeatAction.FOREVER, action))

        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Static.camera.update()
        Static.batch.projectionMatrix = Static.camera.combined

        clearScreen(0f, 0f, 0f)
        stage.act()
        stage.draw()

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit()
    }
}