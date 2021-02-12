package io.github.gabehowe.grayspets

import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class PetsMenu : InventoryHolder {
    val inv = Bukkit.createInventory(this, 54, "Pets")
    override fun getInventory(): Inventory {
        return inv
    }
}