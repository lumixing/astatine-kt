package dev.lumix.astatine.ecs.components

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

// used for pickable upon collision entities
class PickableComponent : Component {
    companion object {
        val mapper = mapperFor<PickableComponent>()
    }
}