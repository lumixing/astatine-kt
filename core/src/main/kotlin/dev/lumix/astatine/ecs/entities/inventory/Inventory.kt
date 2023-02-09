package dev.lumix.astatine.ecs.entities.inventory

import ktx.log.info

class Inventory {
    val array: Array<InventoryItem> = Array(5) { InventoryItem(null, 0) }

    fun addItem(item: InventoryItem) {
        // logic: if !hasItem if isFull dont get item else go thru inv to find 1st empty slot else go thru inv to find 1st item
        if (hasItem(InventoryItem(item.item, 0))) {
            info { "already have this item" }
            val index = array.indices.find { array[it].item == item.item }

            if (index == null) {
                ktx.log.error { "expected to have item (hasItem) but doesnt have! (!index)" }
                return
            }

            info { "incrementing amount" }
            array[index].amount += item.amount
        } else {
            info { "dont have this item" }
            if (isFull()) {
                info { "inv is full, not adding" }
                return
            }
            val index = array.indices.find { array[it].item == null }

            if (index == null) {
                ktx.log.error { "expected to have empty slot (!isFull()) but doesnt have! (!index)" }
                return
            }

            info { "adding item to empty slot" }
            array[index].item = item.item
            array[index].amount = item.amount
        }
    }

    fun hasItem(item: InventoryItem): Boolean {
        return array.any { it.item == item.item }
    }

    fun isFull(): Boolean {
        return !array.any { it.item == null }
    }

    fun clearInventory() {
        array.forEach {
            it.item = null
            it.amount = 0
        }
    }
}