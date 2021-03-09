package io.github.gabehowe.grayspets

import net.minecraft.server.v1_16_R3.*
import org.bukkit.Bukkit
import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.craftbukkit.v1_16_R3.scoreboard.CraftScoreboard
import org.bukkit.entity.*
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.util.Vector
import java.util.*
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.scoreboard.Team

import java.util.ArrayList




abstract class BasePet(
    var entity: Entity,
    val graysPets: GraysPets,
    var isHidden: Boolean = false
) {

    init {
    }


    companion object {
        fun createEntity(graysPets: GraysPets, entityType: EntityType, player: Player, isBaby: Boolean): Entity {
            val loc = player.location.clone()
            loc.x = player.location.x + Math.random() * (3 - -3 + 1) + -3
            loc.z = player.location.z + Math.random() * (3 - -3 + 1) + -3
            loc.y = player.world.rayTraceBlocks(
                Location(player.world, loc.x, player.location.y, loc.z),
                Vector(0.0, -1.0, 0.0),
                5.0,
                FluidCollisionMode.NEVER
            )?.hitPosition?.y ?: player.location.y
            loc.y = loc.y + 1
            val living: LivingEntity = player.world.spawnEntity(loc, entityType) as LivingEntity
            player.persistentDataContainer.set(
                NamespacedKey(graysPets, "active-pet"),
                PersistentDataType.STRING,
                living.uniqueId.toString()
            )
            if (isBaby) {
                (living as Breedable).setBaby()
                living.ageLock = true
            }
            living.isInvulnerable = true
            living.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = 2048.0
            val list = graysPets.petsConfig.getMapList("pets")
            list.add(mutableMapOf(Pair(player.uniqueId.toString(),living.uniqueId.toString())))
            graysPets.petsConfig.set("pets", list)
            graysPets.petsConfig.save(graysPets.petsPath)
            living.health = 2048.0
            living.persistentDataContainer.set(graysPets.petPathfindKey, PersistentDataType.INTEGER, 0)
            living.persistentDataContainer.set(NamespacedKey(graysPets, "is-pet"), PersistentDataType.INTEGER, 1)
            living.persistentDataContainer.set(graysPets.hiddenKey, PersistentDataType.INTEGER, 0)
            living.persistentDataContainer.set(
                graysPets.petOwnerKey,
                PersistentDataType.STRING,
                player.uniqueId.toString()
            )
            if (Bukkit.getScoreboardManager().newScoreboard.getTeam("petsDONOTDELETE") == null) {
                Bukkit.dispatchCommand(living, "team add petsDONOTDELETE")
            }
            Bukkit.dispatchCommand(living,"team join petsDONOTDELETE")
            // TODO Do this, but with packets (sry thomas)
            return living
        }

        fun getOwner(graysPets: GraysPets, pet: Entity): Player? {
            return Bukkit
                    .getPlayer(UUID.fromString(pet.persistentDataContainer.get(graysPets.petOwnerKey,PersistentDataType.STRING)
                )
            )
        }

        fun getOwnerUUID(graysPets: GraysPets, pet: Entity): UUID {
            return UUID.fromString(pet.persistentDataContainer.get(graysPets.petOwnerKey, PersistentDataType.STRING))
        }
    }

    open fun cleanup() {
        isHidden = false
        entity.persistentDataContainer.set(graysPets.petPathfindKey,PersistentDataType.INTEGER,0)
    }
}