package dev.lumix.astatine.ecs.components

import com.badlogic.ashley.core.Component
import dev.lumix.astatine.ecs.entities.inventory.Inventory
import dev.lumix.astatine.ecs.entities.inventory.InventoryItem
import ktx.ashley.mapperFor

class InventoryComponent : Component {
    companion object {
        val mapper = mapperFor<InventoryComponent>()
    }

    val inventory = Inventory()
    val current = 0
}