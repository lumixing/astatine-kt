package dev.lumix.astatine.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.dongbat.jbump.Item
import ktx.ashley.mapperFor

class ItemComponent(val item: Item<Entity>) : Component {
    companion object {
        val mapper = mapperFor<ItemComponent>()
    }
}