package dev.lumix.astatine.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import ktx.ashley.mapperFor

class TransformComponent : Component {
    companion object {
        val mapper = mapperFor<TransformComponent>()
    }

    val position = Vector2()
    var rotation = 0f // in radians
}