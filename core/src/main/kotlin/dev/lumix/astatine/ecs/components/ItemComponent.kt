package dev.lumix.astatine.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.dongbat.jbump.Item
import ktx.ashley.mapperFor

// used for storing the item object of an entity
class ItemComponent(val item: Item<Entity>) : Component {
    companion object {
        val mapper = mapperFor<ItemComponent>()
    }
}