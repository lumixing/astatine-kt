package dev.lumix.astatine.ecs.entities.inventory

class Inventory {
    val array: Array<InventoryItem> = Array(5) { InventoryItem(null, 0) }

    fun addItem(item: InventoryItem) {
        // logic: if !hasItem if isFull dont get item else go thru inv to find 1st empty slot else go thru inv to find 1st item
        if (hasItem(InventoryItem(item.item, 0))) {
            val index = array.indices.find { array[it].item == item.item }

            if (index == null) {
                ktx.log.error { "expected to have item (hasItem) but doesnt have! (!index)" }
                return
            }

            array[index].amount += item.amount
        } else {
            if (isFull()) return
            val index = array.indices.find { array[it].item == null }

            if (index == null) {
                ktx.log.error { "expected to have empty slot (!isFull()) but doesnt have! (!index)" }
                return
            }

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

    fun removeItem(slot: Int, amount: Int): Boolean {
        if (!(slot in 0..5)) return false

        array[slot].amount -= amount
        return true
    }
}