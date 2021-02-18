package io.github.gabehowe.grayspets

import com.destroystokyo.paper.entity.ai.MobGoals
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.nio.file.Paths

class GraysPets : JavaPlugin() {
    val petsPath: File = Paths.get(dataFolder.path, "pets.yml").toFile()
    val petsConfig = YamlConfiguration.loadConfiguration(petsPath)
    override fun onEnable() {
        // Plugin startup logic

        server.pluginManager.registerEvents(PetsEvents(this), this)
        getCommand("petmeowo")?.setExecutor(PetsCommand(this))

    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
    fun initMenu(inv : Inventory, player: Player) {

    }
}