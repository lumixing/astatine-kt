package dev.lumix.astatine.ecs.components

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

// used for block entities
class BlockComponent : Component {
    companion object {
        val mapper = mapperFor<BlockComponent>()
    }
}