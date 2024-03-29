package dev.lumix.astatine.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import dev.lumix.astatine.engine.Static
import dev.lumix.astatine.engine.TextureAtlasAssets
import dev.lumix.astatine.engine.get
import ktx.ashley.mapperFor

// used for rendering a sprite
class SpriteComponent : Component {
    companion object {
        val mapper = mapperFor<SpriteComponent>()
    }

    var sprite = Sprite(Static.assets[TextureAtlasAssets.Game].findRegion("lumix"))
    val offset: Vector2 = Vector2(0f, 0f)
    var flipX = true
    var z = 0
}