package io.github.gabehowe.grayspets

import Pathfind
import com.destroystokyo.paper.entity.ai.Goal
import com.destroystokyo.paper.event.entity.EntityPathfindEvent
import net.minecraft.server.v1_16_R3.*
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.persistence.PersistentDataType
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.FileWriter
import java.util.*
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity

import net.minecraft.server.v1_16_R3.EntityInsentient
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPig
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.entity.Creature
import org.bukkit.entity.LivingEntity


class PetsEvents(private val graysPets: GraysPets) : Listener {
    @EventHandler
    fun onJoinEvent(event: PlayerJoinEvent) {
        val user = mutableMapOf<String, Any>()
        val users = mutableListOf<UUID>()
        users.add(event.player.uniqueId)
        user["players"] = users
        val options = DumperOptions()
        options.indent = 6
        options.indicatorIndent = 4
        options.defaultFlowStyle = DumperOptions.FlowStyle.AUTO

        val yaml = Yaml(options)
        val writer = FileWriter(graysPets.petsPath, true)

        yaml.dump(user, writer)
        graysPets.petsConfig.save(graysPets.petsPath)
        event.player.sendMessage("hello")
    }

    @EventHandler
    fun onPathfind(event: EntityPathfindEvent) {
        val entity = event.entity as Mob
        if (!entity.persistentDataContainer.has(NamespacedKey(graysPets, "pet-pathfind"), PersistentDataType.INTEGER) || entity.persistentDataContainer.get(NamespacedKey(graysPets, "pet-pathfind"), PersistentDataType.INTEGER) == 1) {
            return
        }
        val mobGoals = Bukkit.getMobGoals()
        val pathfinder = entity.pathfinder
        entity.persistentDataContainer.set(NamespacedKey(graysPets, "pet-pathfind"), PersistentDataType.INTEGER, 1)
        Bukkit.getServer().broadcastMessage("hello")
        val speed = 16.0
        pathfinder.moveTo(Bukkit.getPlayer("Xantholeucophore") as LivingEntity)
        mobGoals.getAllGoals(entity)
        Bukkit.getScheduler().runTaskLater(graysPets, Runnable { (entity).persistentDataContainer.set(NamespacedKey(graysPets, "pet-pathfind"), PersistentDataType.INTEGER, 0) }, 20L)

    }
}