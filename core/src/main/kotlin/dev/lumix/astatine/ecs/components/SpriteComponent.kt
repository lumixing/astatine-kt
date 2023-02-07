package dev.lumix.astatine.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import dev.lumix.astatine.engine.Static
import dev.lumix.astatine.engine.TextureAtlasAssets
import dev.lumix.astatine.engine.get
import ktx.ashley.mapperFor

class SpriteComponent : Component {
    companion object {
        val mapper = mapperFor<SpriteComponent>()
    }

    val sprite = Sprite(Static.assets[TextureAtlasAssets.Game].findRegion("lumix"))
    val offset: Vector2 = Vector2.Zero
    var z = 0
}