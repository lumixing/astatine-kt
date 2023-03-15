package dev.lumix.astatine.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import ktx.ashley.mapperFor

// used for entities affected by physics
class PhysicsComponent : Component {
    companion object {
        val mapper = mapperFor<PhysicsComponent>()
    }

    val velocity = Vector2()
    val bounds = Vector2()
}