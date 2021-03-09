package io.github.gabehowe.grayspets

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent
import io.github.gabehowe.grayspets.pet.HoppyPet
import io.github.gabehowe.grayspets.pet.VanityUltimate
import io.papermc.paper.event.entity.EntityMoveEvent
import net.minecraft.server.v1_16_R3.EntityLightning
import net.minecraft.server.v1_16_R3.EntityTypes
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntity
import org.bukkit.*
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.EntityPortalEnterEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.event.entity.EntityTeleportEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import java.util.*


class PetsEvents(private val graysPets: GraysPets) : Listener {

    @EventHandler
    fun onJoinEvent(event: PlayerJoinEvent) {
        event.player.persistentDataContainer.set(NamespacedKey(graysPets,"jim"), PersistentDataType.INTEGER, 1)
        if (!event.player.persistentDataContainer.has(
                graysPets.activePetKey, PersistentDataType.STRING
            )
        ) {
            return
        }
        if (graysPets.petMap[event.player.uniqueId] == null) {
            return
        }
        if (Bukkit.getEntity(
                UUID.fromString(
                    event.player.persistentDataContainer.get(
                        graysPets.activePetKey,
                        PersistentDataType.STRING
                    )
                )
            ) == null
        ) {
            PetFactory.makePet(
                PetFactory.PetType.valueOf(
                    graysPets.petMap[event.player.uniqueId]!!.entity.persistentDataContainer.get(
                        graysPets.petTypeKey,
                        PersistentDataType.STRING
                    )!!
                ), graysPets, event.player
            )
            return
        }
        val pet = graysPets.petMap[event.player.uniqueId] ?: return
        if (!pet.isHidden) {
            pet.entity.teleport(event.player.location)
            (pet.entity as LivingEntity).setAI(true)
            (pet.entity as LivingEntity).isInvisible = false
            val ent = Bukkit.getEntity(
                UUID.fromString(
                    event.player.persistentDataContainer.get(
                        graysPets.activePetKey,
                        PersistentDataType.STRING
                    )
                )
            )
                ?: return
            val isHidden: Boolean =
                ent.persistentDataContainer.get(graysPets.hiddenKey, PersistentDataType.INTEGER) == 1
            PetFactory.loadPet(
                PetFactory.PetType.valueOf(ent.persistentDataContainer[graysPets.petTypeKey, PersistentDataType.STRING]!!),
                ent,
                graysPets,
                isHidden
            )
        }
    }

    @EventHandler
    fun onMoveEvent(event: EntityMoveEvent) {
        if (event.entityType == EntityType.PLAYER) {
            val owner = event.entity as Player
            if (!owner.persistentDataContainer.has(graysPets.activePetKey, PersistentDataType.STRING)) {
                return
            }
            if (!owner.persistentDataContainer.has(graysPets.petTypeKey, PersistentDataType.STRING)) {
                return
            }
            if (Bukkit.getEntity(UUID.fromString(owner.persistentDataContainer.get(graysPets.activePetKey, PersistentDataType.STRING))) == null) {
                PetFactory.makePet(PetFactory.PetType.valueOf(owner.persistentDataContainer.get(graysPets.petTypeKey, PersistentDataType.STRING)!!),graysPets,owner)
                return
            }
            if (graysPets.petMap[owner.uniqueId] == null) {
                PetFactory.makePet(PetFactory.PetType.valueOf(owner.persistentDataContainer.get(graysPets.petTypeKey, PersistentDataType.STRING)!!), graysPets, owner)
            }
            val petHandler = graysPets.petMap[owner.uniqueId]!!
            if (petHandler.isHidden) {
                petHandler.entity.teleport(Location(owner.world,owner.location.x,
                        owner.location.y + 10000,
                        owner.location.z
                    )
                )
                return
            }
            val pet = petHandler.entity as Mob
            if (!pet.persistentDataContainer.has(
                    graysPets.petPathfindKey, PersistentDataType.INTEGER
                ) || pet.persistentDataContainer.get(
                    graysPets.petPathfindKey, PersistentDataType.INTEGER
                ) == 1
            ) {
                return
            }
            if (pet.world != owner.world) {
                val loc = owner.location.clone()
                loc.x = owner.location.x + Math.random() * (4 - -4 + 1) + -4
                loc.z = owner.location.z + Math.random() * (4 - -4 + 1) + -4
                pet.teleport(loc)
            }
            if (pet.location.distance(owner.location) > graysPets.tpRange) {
                val loc = owner.location.clone()
                loc.x = owner.location.x + Math.random() * (4 - -4 + 1) + -4
                loc.z = owner.location.z + Math.random() * (4 - -4 + 1) + -4
                pet.teleport(loc)
            }
            val loc = owner.location.clone()
            val pathfinder = pet.pathfinder
            loc.x = owner.location.x + Math.random() * (3 - -3 + 1) + -3
            loc.z = owner.location.z + Math.random() * (3 - -3 + 1) + -3
            pathfinder.moveTo(loc, 1.0)
            Bukkit.getScheduler().runTaskLater(
                graysPets, Runnable {
                    (pet).persistentDataContainer.set(
                        graysPets.petPathfindKey, PersistentDataType.INTEGER, 0
                    )
                }, 10L
            )
            return
        }
        val pet = event.entity as Mob
        if (!pet.persistentDataContainer.has(
                graysPets.petPathfindKey, PersistentDataType.INTEGER
            ) || pet.persistentDataContainer.get(
                graysPets.petPathfindKey, PersistentDataType.INTEGER
            ) == 1
        ) {
            return
        }
        if (!pet.persistentDataContainer.has(graysPets.petOwnerKey, PersistentDataType.STRING)) {
            return
        }
        val owner = Bukkit.getPlayer(
            UUID.fromString(pet.persistentDataContainer.get(graysPets.petOwnerKey, PersistentDataType.STRING)!!)
        )
        if (owner == null) {
            pet.setAI(false)
            pet.teleport(Location(pet.world,pet.location.x,Math.random() * (10000 - 1000 + 1) + 1000,pet.location.z))
            return

        }
        if (graysPets.petMap[owner.uniqueId] == null) {
            PetFactory.loadPet(PetFactory.PetType.valueOf(owner.persistentDataContainer.get(graysPets.petTypeKey, PersistentDataType.STRING)!!),pet, graysPets, pet.persistentDataContainer.get(graysPets.hiddenKey, PersistentDataType.INTEGER) == 1)
        }
        if (graysPets.petMap[owner.uniqueId]!!.isHidden) {
            return
        }
        val pathfinder = pet.pathfinder
        pet.persistentDataContainer.set(graysPets.petPathfindKey, PersistentDataType.INTEGER, 1)
        if (pet.world != owner.world) {
            val loc = owner.location.clone()
            loc.x = owner.location.x + Math.random() * (4 - -4 + 1) + -4
            loc.z = owner.location.z + Math.random() * (4 - -4 + 1) + -4
            pet.teleport(loc)
        }
        if (pet.location.distance(owner.location) > graysPets.tpRange) {
            val loc = owner.location.clone()
            loc.x = owner.location.x + Math.random() * (4 - -4 + 1) + -4
            loc.z = owner.location.z + Math.random() * (4 - -4 + 1) + -4
            pet.teleport(loc)
        }
        val loc = owner.location.clone()
        loc.x = owner.location.x + Math.random() * (3 - -3 + 1) + -3
        loc.z = owner.location.z + Math.random() * (3 - -3 + 1) + -3
        pathfinder.moveTo(loc, 1.0)
        Bukkit.getScheduler().runTaskLater(
            graysPets, Runnable {
                (pet).persistentDataContainer.set(
                    graysPets.petPathfindKey, PersistentDataType.INTEGER, 0
                )
            }, 10L
        )
    }

    @EventHandler
    fun onBearStalkFoxEvent(event: EntityTargetEvent) {
        if (!event.entity.persistentDataContainer.has(
                graysPets.petPathfindKey, PersistentDataType.INTEGER
            )
        ) {
            return
        }
        event.isCancelled = true
    }

    @EventHandler
    fun onFoxFreakingStealYourScaffoldingEvent(event: EntityPickupItemEvent) {
        if (!event.entity.persistentDataContainer.has(
                graysPets.petPathfindKey, PersistentDataType.INTEGER
            )
        ) {
            return
        }
        event.isCancelled = true
    }

    @EventHandler
    fun onLeaveEvent(event: PlayerQuitEvent) {
        if (!event.player.persistentDataContainer.has(graysPets.activePetKey, PersistentDataType.STRING)) {
            return
        }
        if (Bukkit.getEntity(
                UUID.fromString(
                    event.player.persistentDataContainer.get(
                        graysPets.activePetKey,
                        PersistentDataType.STRING
                    )
                )
            ) == null
        ) {
            return
        }
        if (graysPets.petMap[event.player.uniqueId] == null) {
            return
        }
        val pet = graysPets.petMap[event.player.uniqueId]!!
        pet.entity.teleport(
            Location(
                event.player.world,
                event.player.location.x,
                Math.random() * (10000 - 1000 + 1) + 1000,
                event.player.location.z
            )
        )
        (pet.entity as LivingEntity).setAI(false)

    }

    @EventHandler
    fun onPlayerTeleportEvent(event: EntityTeleportEvent) {
        if (event.entityType != EntityType.PLAYER) {
            return
        }
        val owner = event.entity
        if (!owner.persistentDataContainer.has(graysPets.activePetKey, PersistentDataType.STRING)) {
            return
        }
        if (Bukkit.getEntity(
                UUID.fromString(
                    owner.persistentDataContainer.get(
                        graysPets.activePetKey, PersistentDataType.STRING
                    )
                )
            ) == null
        ) {
            return
        }
        val pet = Bukkit.getEntity(
            UUID.fromString(
                owner.persistentDataContainer.get(
                    graysPets.activePetKey, PersistentDataType.STRING
                )
            )
        )!! as Mob
        if (!pet.persistentDataContainer.has(
                graysPets.petPathfindKey, PersistentDataType.INTEGER
            )
        ) {
            return
        }
        val loc = owner.location.clone()
        loc.x = owner.location.x + Math.random() * (4 - -4 + 1) + -4
        loc.z = owner.location.z + Math.random() * (4 - -4 + 1) + -4
        pet.teleport(loc)
        Bukkit.getScheduler().runTaskLater(
            graysPets, Runnable {
                (pet).persistentDataContainer.set(
                    graysPets.petPathfindKey, PersistentDataType.INTEGER, 0
                )
            }, 10L
        )
        return
    }

    val hoppyLocations = mutableSetOf<Location>()

    @EventHandler
    fun onHoppyTheRabbitMove(event: EntityMoveEvent) {
        if (!event.entity.persistentDataContainer.has(graysPets.petOwnerKey, PersistentDataType.STRING)) {
            return
        }
        val pet = graysPets.petMap[UUID.fromString(
            event.entity.persistentDataContainer.get(
                graysPets.petOwnerKey,
                PersistentDataType.STRING
            )
        )] ?: return
        if (pet.entity.persistentDataContainer.get(
                graysPets.petCooldownKey, PersistentDataType.INTEGER
            ) == 1
        ) {
            return
        }
        if (pet !is HoppyPet) {
            return
        }
        pet.spawnFlower(hoppyLocations)

    }


    @EventHandler
    fun onPlayerActivateVanityUltimateEvent(event: PlayerInteractEvent) {
        val item = event.item ?: return
        if (!item.itemMeta.persistentDataContainer.has(graysPets.stickKey, PersistentDataType.INTEGER)) {
            return
        }
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }
        if (!event.player.persistentDataContainer.has(graysPets.activePetKey, PersistentDataType.STRING)) {
            return
        }

        val pet = graysPets.petMap[event.player.uniqueId] ?: return
        if (pet.isHidden) {
            return
        }
        if (pet.entity.persistentDataContainer.get(
                graysPets.petCooldownKey, PersistentDataType.INTEGER
            ) == 1
        ) {
            return
        }
        event.isCancelled = true
        if (pet is VanityUltimate) {
            pet.activateUltimate()
        }
    }

    @EventHandler
    fun onDimensionEvent(event: EntityPortalEnterEvent) {
        if (event.entityType != EntityType.PLAYER) {
            return
        }
        val owner = event.entity
        if (!owner.persistentDataContainer.has(NamespacedKey(graysPets, "active-pet"), PersistentDataType.STRING)) {
            return
        }
        if (Bukkit.getEntity(
                UUID.fromString(
                    owner.persistentDataContainer.get(
                        NamespacedKey(graysPets, "active-pet"), PersistentDataType.STRING
                    )
                )
            ) == null
        ) {
            return
        }
        val pet = Bukkit.getEntity(
            UUID.fromString(
                owner.persistentDataContainer.get(
                    NamespacedKey(graysPets, "active-pet"), PersistentDataType.STRING
                )
            )
        )!! as Mob
        if (!pet.persistentDataContainer.has(
                graysPets.petPathfindKey, PersistentDataType.INTEGER
            ) || pet.persistentDataContainer.get(
                graysPets.petPathfindKey, PersistentDataType.INTEGER
            ) == 1
        ) {
            return
        }
        val loc = owner.location.clone()
        loc.x = owner.location.x + Math.random() * (4 - -4 + 1) + -4
        loc.z = owner.location.z + Math.random() * (4 - -4 + 1) + -4
        pet.teleport(loc)
        Bukkit.getScheduler().runTaskLater(
            graysPets, Runnable {
                (pet).persistentDataContainer.set(
                    graysPets.petPathfindKey, PersistentDataType.INTEGER, 0
                )
            }, 10L
        )
        return

    }

    @EventHandler
    fun entityCollideEvent(event: ProjectileCollideEvent) {
        if (event.entity.type != EntityType.ARROW) {
            return
        }
        if (!event.entity.persistentDataContainer.has(
                NamespacedKey(graysPets, "chelsea-uuid"), PersistentDataType.STRING
            )
        ) {
            return
        }
        val pet = Bukkit.getEntity(
            UUID.fromString(
                event.entity.persistentDataContainer.get(
                    NamespacedKey(
                        graysPets, "chelsea-uuid"
                    ), PersistentDataType.STRING
                )
            )
        )!!
        pet.velocity = Vector(
            pet.location.x - event.collidedWith.location.x,
            pet.location.y - event.collidedWith.location.y,
            pet.location.z - event.collidedWith.location.z
        )
        val bolt = EntityLightning(EntityTypes.LIGHTNING_BOLT, (event.entity.world as CraftWorld).handle)
        val playersWhoSee = pet.location.getNearbyEntitiesByType(Player::class.java, 50.0)
        event.entity.remove()
        for (i in playersWhoSee) {
            val task = Bukkit.getScheduler().runTaskTimer(graysPets, Runnable {
                val loc = event.collidedWith.location.clone()
                loc.x = event.collidedWith.location.x + Math.random() * (1 - -1 + 1) + -1
                loc.z = event.collidedWith.location.z + Math.random() * (1 - -1 + 1) + -1
                bolt.setLocation(loc.x, loc.y, loc.z, pet.location.yaw, pet.location.pitch)
                val packetSpawn = PacketPlayOutSpawnEntity(bolt)
                (i as CraftPlayer).handle.playerConnection.sendPacket(packetSpawn)
                (i as Player).world.playSound(
                    event.collidedWith.location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2.0f, 1.0f
                )
                i.world.spawnParticle(Particle.EXPLOSION_HUGE, loc, 2)
            }, 2, 0)

            Bukkit.getScheduler()
                .runTaskLater(graysPets, Runnable { Bukkit.getScheduler().cancelTask(task.taskId) }, 40L)
        }
    }


}