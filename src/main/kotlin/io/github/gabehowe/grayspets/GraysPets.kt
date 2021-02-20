package io.github.gabehowe.grayspets

import com.destroystokyo.paper.entity.ai.MobGoals
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Entity
import org.bukkit.entity.Fox
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.nio.file.Paths
import java.util.*

class GraysPets : JavaPlugin() {
    val petsPath: File = Paths.get(dataFolder.path, "pets.yml").toFile()
    val petsConfig = YamlConfiguration.loadConfiguration(petsPath)
    val acceptablePetsList = mutableListOf<String>()
    val tprange: Double
        get() {
            return config.get("pet-tprange") as Double? ?: 15.0
        }
    val petMap = mutableMapOf<UUID, Entity>()
    val range: Double
        get() {
            return config.get("pet-range") as Double? ?: 5.0
        }

    override fun onEnable() {
        acceptablePets()
        server.pluginManager.registerEvents(PetsEvents(this), this)
        getCommand("pet")?.setExecutor(PetsCommand(this))
        saveDefaultConfig()

    }

    override fun onDisable() {
        for (pair in petMap) {
            if (pair.value.persistentDataContainer.has(
                    NamespacedKey(this, "flower-cooldown"),
                    PersistentDataType.INTEGER
                )
            ) {
                pair.value.persistentDataContainer.set(
                    NamespacedKey(this, "flower-cooldown"),
                    PersistentDataType.INTEGER,
                    0
                )
            }
            if (pair.value.persistentDataContainer.has(
                    NamespacedKey(this, "pet-pathfind"),
                    PersistentDataType.INTEGER
                )
            ) {
                pair.value.persistentDataContainer.set(
                    NamespacedKey(this, "pet-pathfind"),
                    PersistentDataType.INTEGER,
                    0
                )
            }
            if (pair.value.persistentDataContainer.has(
                    NamespacedKey(this, "pet-cooldown"),
                    PersistentDataType.INTEGER
                )
            ) {
                pair.value.persistentDataContainer.set(
                    NamespacedKey(this, "pet-cooldown"),
                    PersistentDataType.INTEGER,
                    0
                )
            }
            if (pair.value.persistentDataContainer.has(NamespacedKey(this, "pet-type"), PersistentDataType.STRING)) {
                if (pair.value.persistentDataContainer.get(
                        NamespacedKey(this, "pet-type"),
                        PersistentDataType.STRING
                    ) == "kitsune"
                ) {
                    (pair.value as Fox).setAI(true)
                    (pair.value as Fox).isInvisible = false
                }

            }
        }
    }

    fun initMenu(inv: Inventory, player: Player) {

    }

    fun acceptablePets() {
        acceptablePetsList.add("baby_chicken")
        acceptablePetsList.add("baby_cow")
        acceptablePetsList.add("baby_donkey")
        acceptablePetsList.add("baby_horse")
        acceptablePetsList.add("baby_mooshroom")
        acceptablePetsList.add("baby_ocelot")
        acceptablePetsList.add("baby_pig")
        acceptablePetsList.add("baby_bear")
        acceptablePetsList.add("baby_rabbit")
        acceptablePetsList.add("baby_sheep")
        acceptablePetsList.add("baby_turtle")
        acceptablePetsList.add("baby_fox")
        acceptablePetsList.add("baby_bee")
        acceptablePetsList.add("baby_panda")
        acceptablePetsList.add("baby_brown_panda")
        acceptablePetsList.add("baby_gabriel_polemistis")
        acceptablePetsList.add("ocelot")
        acceptablePetsList.add("pig")
        acceptablePetsList.add("rabbit")
        acceptablePetsList.add("turtle")
        acceptablePetsList.add("bee")
        acceptablePetsList.add("fox")
        acceptablePetsList.add("chicken")
        acceptablePetsList.add("gray_kitsune")
        acceptablePetsList.add("thomas_chelsea")
        acceptablePetsList.add("carissa_hoppy")
        acceptablePetsList.add("katie_nick")
        acceptablePetsList.add("pog_cat")
        acceptablePetsList.add("gabriel_polemistis")
        acceptablePetsList.add("baby_gray_kitsune")
        acceptablePetsList.add("baby_thomas_chelsea")
        acceptablePetsList.add("baby_carissa_hoppy")
        acceptablePetsList.add("baby_katie_nick")
        acceptablePetsList.add("baby_pog_cat")
    }
}