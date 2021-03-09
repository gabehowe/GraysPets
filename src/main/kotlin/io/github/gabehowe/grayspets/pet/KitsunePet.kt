package io.github.gabehowe.grayspets.pet

import io.github.gabehowe.grayspets.BasePet
import io.github.gabehowe.grayspets.GraysPets
import io.github.gabehowe.grayspets.PetFactory
import net.minecraft.server.v1_16_R3.*
import org.bukkit.*
import org.bukkit.Particle
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Fox
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector

class KitsunePet(entity: Entity, graysPets: GraysPets, isHidden : Boolean) : BasePet(entity, graysPets, isHidden), VanityUltimate {

    override fun cleanup() {
        super.cleanup()
        entity.persistentDataContainer.set(graysPets.petCooldownKey, PersistentDataType.INTEGER, 0)

    }

    override fun activateUltimate() {
        val entity = entity as Fox
        entity.velocity = Vector(0, 1, 0)
        if (entity.equipment != null) {
            entity.equipment?.clear()
        }
        val petY = entity.location.y + 5
        val dragon = EntityEnderDragon(EntityTypes.ENDER_DRAGON, (entity.world as CraftWorld).handle)
        val packetPlayOutEntityDestroy = PacketPlayOutEntityDestroy(dragon.id)
        val playersWhoSee = entity.location.getNearbyEntitiesByType(Player::class.java, 50.0)
        entity.persistentDataContainer.set(NamespacedKey(graysPets, "pet-cooldown"), PersistentDataType.INTEGER, 1)
        Bukkit.getScheduler().runTaskLater(graysPets, Runnable {
            val loc = entity.location
            dragon.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.yaw, loc.yaw)
            val newLoc = entity.location
            newLoc.y = 1000.0
            entity.teleport(newLoc)
            val packetSpawn = PacketPlayOutSpawnEntityLiving(dragon)
            val packet = PacketPlayOutEntityStatus(dragon, 3.toByte())
            entity.world.spawnParticle(Particle.EXPLOSION_HUGE, entity.location, 10)
            entity.world.playSound(entity.location, Sound.ENTITY_ENDER_DRAGON_DEATH, 2.0f, 1.0f)
            entity.world.playSound(entity.location, Sound.ENTITY_ENDER_DRAGON_DEATH, 2.0f, 1.0f)
            (entity as Fox).setAI(false)
            for (i in playersWhoSee) {
                (i as CraftPlayer).handle.playerConnection.sendPacket(packetSpawn)
                i.handle.playerConnection.sendPacket(packet)
                i.playSound(entity.location, Sound.ENTITY_ENDER_DRAGON_DEATH, 2.0f, 1.0f)
            }
            entity.isInvisible = true
        }, 10L)
        Bukkit.getScheduler().runTaskLater(graysPets, Runnable {
            entity.persistentDataContainer.set(
                graysPets.petPathfindKey, PersistentDataType.INTEGER, 0
            )

            entity.teleport(Location(entity.location.world, entity.location.x, petY, entity.location.z))
            for (i in playersWhoSee) {
                (i as CraftPlayer).handle.playerConnection.sendPacket(packetPlayOutEntityDestroy)
            }
            entity.isInvisible = false
            entity.setAI(true)
        }, 220L)
        Bukkit.getScheduler().runTaskLater(
            graysPets, Runnable {
                entity.persistentDataContainer.set(
                    NamespacedKey(graysPets, "pet-cooldown"), PersistentDataType.INTEGER, 0
                )
            }, 240L
        )
    }

    companion object {

        fun create(petType: PetFactory.PetType, graysPets: GraysPets, player: Player, isBaby: Boolean): Entity {
            val entity = createEntity(graysPets, EntityType.FOX, player, isBaby)
            entity as Fox
            entity.customName = "§6Kitsune"
            entity.foxType = Fox.Type.RED
            entity.firstTrustedPlayer = player
            entity.persistentDataContainer.set(graysPets.petCooldownKey, PersistentDataType.INTEGER, 0)
            entity.persistentDataContainer.set(graysPets.petTypeKey, PersistentDataType.STRING, "$petType")
            player.persistentDataContainer.set(graysPets.petTypeKey, PersistentDataType.STRING, "$petType")
            return entity
        }
    }

}