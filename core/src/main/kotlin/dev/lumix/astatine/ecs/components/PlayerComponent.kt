package dev.lumix.astatine.ecs.components

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

// used for you
class PlayerComponent : Component {
    companion object {
        val mapper = mapperFor<PlayerComponent>()
    }

    var isJumping = false
    var jumpTime = 0f
}