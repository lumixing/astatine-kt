package dev.lumix.astatine.ecs.components

import com.badlogic.ashley.core.Component
import dev.lumix.astatine.ecs.entities.inventory.Inventory
import ktx.ashley.mapperFor

// used for entities with an inventory
class InventoryComponent : Component {
    companion object {
        val mapper = mapperFor<InventoryComponent>()
    }

    val inventory = Inventory()
    val current = 0
}