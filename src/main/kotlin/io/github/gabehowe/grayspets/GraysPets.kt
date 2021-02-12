package io.github.gabehowe.grayspets

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin

class GraysPets : JavaPlugin() {
    override fun onEnable() {
        // Plugin startup logic
        server.pluginManager.registerEvents(PetsEvents(this), this)

    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
    fun initMenu(inv : Inventory, player: Player) {

    }
}