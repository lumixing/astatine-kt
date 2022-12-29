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

    // todo: add default error texture like mc?
    val sprite = Sprite(Static.assets[TextureAtlasAssets.Game].findRegion("lumix"))
    val offset = Vector2(0f, 0f)
    var z = 0
}