package dev.lumix.astatine.ecs.components

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

// used for explosions boom
class ExplosiveComponent : Component {
    companion object {
        val mapper = mapperFor<ExplosiveComponent>()
    }
}