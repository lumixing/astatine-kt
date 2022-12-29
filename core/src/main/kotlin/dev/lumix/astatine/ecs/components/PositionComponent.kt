package dev.lumix.astatine.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import ktx.ashley.mapperFor

class PositionComponent : Component {
    companion object {
        val mapper = mapperFor<PositionComponent>()
    }

    val position = Vector2()
    var angle = 0f // radians
}