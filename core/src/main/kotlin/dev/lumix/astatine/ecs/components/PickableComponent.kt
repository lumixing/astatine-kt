package dev.lumix.astatine.ecs.components

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

class PickableComponent : Component {
    companion object {
        val mapper = mapperFor<PickableComponent>()
    }

    val range = 0f
}