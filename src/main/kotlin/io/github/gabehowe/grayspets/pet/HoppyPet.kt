package io.github.gabehowe.grayspets.pet

import io.github.gabehowe.grayspets.BasePet
import io.github.gabehowe.grayspets.GraysPets
import io.github.gabehowe.grayspets.PetFactory
import net.minecraft.server.v1_16_R3.Block
import net.minecraft.server.v1_16_R3.BlockPosition
import net.minecraft.server.v1_16_R3.PacketPlayOutBlockChange
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_16_R3.util.CraftVector
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Rabbit
import org.bukkit.persistence.PersistentDataType

class HoppyPet(entity: Entity, graysPets: GraysPets, isHidden : Boolean) : BasePet(entity, graysPets, isHidden) {


    override fun cleanup() {
        super.cleanup()
        entity.persistentDataContainer.set(graysPets.petCooldownKey, PersistentDataType.INTEGER, 0)
    }

    companion object {

        fun create(petType: PetFactory.PetType, graysPets: GraysPets, player: Player, isBaby: Boolean): Entity {
            val entity = createEntity(graysPets, EntityType.RABBIT, player, isBaby)
            entity as Rabbit
            entity.customName = "§dHoppy"
            entity.rabbitType = Rabbit.Type.BLACK_AND_WHITE
            entity.persistentDataContainer.set(graysPets.petCooldownKey, PersistentDataType.INTEGER, 0)
            entity.persistentDataContainer.set(graysPets.petTypeKey, PersistentDataType.STRING, "$petType")
            player.persistentDataContainer.set(graysPets.petTypeKey, PersistentDataType.STRING, "$petType")
            return entity
        }
    }

    fun spawnFlower(hoppyLocations : MutableSet<Location>) {
        if (entity.world.getBlockAt(entity.location).type != Material.AIR) {
            return
        }
        val loc = entity.location.block.location
        if (hoppyLocations.contains(loc)) {
            return
        }
        if (!loc.world.getBlockAt(loc.clone().add(0.0, -1.0, 0.0)).isSolid) {
            return
        }
        hoppyLocations.add(loc)
        val packetPlayOutBlockChange = PacketPlayOutBlockChange(
            BlockPosition((CraftVector.toNMS(loc.toVector()))), Block.getByCombinedId((Math.random() * (1422 - 1412 + 1) + 1412).toInt())
        )
        val playersWhoSee = entity.location.getNearbyEntitiesByType(Player::class.java, 50.0)
        entity.persistentDataContainer.set(graysPets.petCooldownKey, PersistentDataType.INTEGER, 1)
        for (i in playersWhoSee) {
            (i as CraftPlayer).handle.playerConnection.sendPacket(packetPlayOutBlockChange)
        }
        Bukkit.getScheduler().runTaskLater(
            graysPets, Runnable {
                entity.persistentDataContainer.set(
                    graysPets.petCooldownKey, PersistentDataType.INTEGER, 0
                )
            }, 10L
        )
        Bukkit.getScheduler().runTaskLater(graysPets, Runnable {
            if (entity.world.getBlockAt(loc).type == Material.AIR) {
                val packetPlayOut = PacketPlayOutBlockChange(
                    BlockPosition((CraftVector.toNMS(loc.toVector()))), Block.getByCombinedId(0)
                )
                for (i in playersWhoSee) {
                    (i as CraftPlayer).handle.playerConnection.sendPacket(packetPlayOut)
                }
                hoppyLocations.remove(loc)
            }
        }, 5 * 20L)
    }
}