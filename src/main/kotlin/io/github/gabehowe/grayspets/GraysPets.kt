package io.github.gabehowe.grayspets

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.nio.file.Paths
import java.util.*

class GraysPets : JavaPlugin() {

    val petsPath: File = Paths.get(dataFolder.path, "pets.yml").toFile()
    val petsConfig = YamlConfiguration.loadConfiguration(petsPath)
    val tpRange: Double
        get() {
            return config.get("pet-tprange") as Double? ?: 15.0
        }
    val petMap = mutableMapOf<UUID, BasePet>()
    val range: Double
        get() {
            return config.get("pet-range") as Double? ?: 5.0
        }
    val petConfigList = mutableListOf<String>()
    val petTypeKey = NamespacedKey(this, "pet-type")
    val petPathfindKey = NamespacedKey(this, "pet-pathfind")
    val petCooldownKey = NamespacedKey(this, "pet-cooldown")
    val activePetKey = NamespacedKey(this, "active-pet")
    val petOwnerKey = NamespacedKey(this, "pet-owner")
    val stickKey = NamespacedKey(this, "stick")
    val hiddenKey = NamespacedKey(this, "isHidden")
    override fun onEnable() {
        server.pluginManager.registerEvents(PetsEvents(this), this)
        getCommand("pet")?.setExecutor(PetsCommand(this))
        saveDefaultConfig()
        for (i in Bukkit.getOnlinePlayers()) {
            if (i.persistentDataContainer.has(activePetKey, PersistentDataType.STRING)) {
                val ent = Bukkit.getEntity(UUID.fromString(i.persistentDataContainer.get(activePetKey, PersistentDataType.STRING))) ?: continue
                val isHidden : Boolean = ent.persistentDataContainer.get(hiddenKey, PersistentDataType.INTEGER) == 1
                PetFactory.loadPet(PetFactory.PetType.valueOf(ent.persistentDataContainer[petTypeKey, PersistentDataType.STRING]!!), ent, this, isHidden)
            }
        }

    }

    override fun onDisable() {
        for (pair in petMap) {
            pair.value.cleanup()
        }
    }
}
