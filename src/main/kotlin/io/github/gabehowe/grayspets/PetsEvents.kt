package io.github.gabehowe.grayspets

import com.destroystokyo.paper.NamespacedTag
import com.destroystokyo.paper.event.entity.ProjectileCollideEvent
import io.papermc.paper.event.entity.EntityMoveEvent
import net.minecraft.server.v1_16_R3.*
import org.bukkit.*
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Tag
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_16_R3.util.CraftVector
import org.bukkit.entity.*
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPortalEnterEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.event.entity.EntityTeleportEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.FileWriter
import java.util.*


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
        if (!event.player.persistentDataContainer.has(
                NamespacedKey(graysPets, "active-pet"),
                PersistentDataType.STRING
            )
        ) {
            return
        }
        val pet = Bukkit.getEntity(
            UUID.fromString(
                event.player.persistentDataContainer.get(
                    NamespacedKey(graysPets, "active-pet"),
                    PersistentDataType.STRING
                )
            )
        ) ?: return
        pet.teleport(event.player.location)
        (pet as LivingEntity).setAI(true)
    }

    @EventHandler
    fun onMoveEvent(event: EntityMoveEvent) {
        if (event.entityType == EntityType.PLAYER) {
            val owner = event.entity
            if (!owner.persistentDataContainer.has(NamespacedKey(graysPets, "active-pet"), PersistentDataType.STRING)) {
                return
            }
            if (Bukkit.getEntity(
                    UUID.fromString(
                        owner.persistentDataContainer.get(
                            NamespacedKey(
                                graysPets,
                                "active-pet"
                            ), PersistentDataType.STRING
                        )
                    )
                ) == null
            ) {
                return
            }
            val pet = Bukkit.getEntity(
                UUID.fromString(
                    owner.persistentDataContainer.get(
                        NamespacedKey(
                            graysPets,
                            "active-pet"
                        ), PersistentDataType.STRING
                    )
                )
            )!! as Mob
            if (!pet.persistentDataContainer.has(
                    NamespacedKey(graysPets, "pet-pathfind"),
                    PersistentDataType.INTEGER
                ) || pet.persistentDataContainer.get(
                    NamespacedKey(graysPets, "pet-pathfind"),
                    PersistentDataType.INTEGER
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
            if (pet.location.distance(owner.location) > graysPets.tprange) {
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
                graysPets,
                Runnable {
                    (pet).persistentDataContainer.set(
                        NamespacedKey(graysPets, "pet-pathfind"),
                        PersistentDataType.INTEGER,
                        0
                    )
                },
                10L
            )
            return
        }
        val pet = event.entity as Mob
        if (!pet.persistentDataContainer.has(
                NamespacedKey(graysPets, "pet-pathfind"),
                PersistentDataType.INTEGER
            ) || pet.persistentDataContainer.get(
                NamespacedKey(graysPets, "pet-pathfind"),
                PersistentDataType.INTEGER
            ) == 1
        ) {
            return
        }
        if (!pet.persistentDataContainer.has(graysPets.petOwnerKey, PersistentDataType.STRING)) {
            return
        }
        val owner = Bukkit.getPlayer(
            UUID.fromString(
                pet.persistentDataContainer.get(
                    NamespacedKey(graysPets, "pet-owner"),
                    PersistentDataType.STRING
                )!!
            )
        )!!
        val pathfinder = pet.pathfinder
        pet.persistentDataContainer.set(graysPets.petPathfindKey, PersistentDataType.INTEGER, 1)
        if (pet.world != owner.world) {
            val loc = owner.location.clone()
            loc.x = owner.location.x + Math.random() * (4 - -4 + 1) + -4
            loc.z = owner.location.z + Math.random() * (4 - -4 + 1) + -4
            pet.teleport(loc)
        }
        if (pet.location.distance(owner.location) > graysPets.tprange) {
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
            graysPets,
            Runnable {
                (pet).persistentDataContainer.set(
                    NamespacedKey(graysPets, "pet-pathfind"),
                    PersistentDataType.INTEGER,
                    0
                )
            },
            10L
        )
    }

    @EventHandler
    fun onBearStalkFoxEvent(event: EntityTargetEvent) {
        if (!event.entity.persistentDataContainer.has(NamespacedKey(graysPets, "is-pet"), PersistentDataType.INTEGER)) {
            return
        }
        event.isCancelled = true
    }

    @EventHandler
    fun onLeaveEvent(event: PlayerQuitEvent) {
        if (!event.player.persistentDataContainer.has(
                NamespacedKey(graysPets, "active-pet"),
                PersistentDataType.STRING
            )
        ) {
            return
        }
        val pet = Bukkit.getEntity(
            UUID.fromString(
                event.player.persistentDataContainer.get(
                    NamespacedKey(graysPets, "active-pet"),
                    PersistentDataType.STRING
                )
            )
        ) ?: return
        pet.teleport(
            Location(
                event.player.world,
                event.player.location.x,
                Math.random() * (10000 - 1000 + 1) + 1000,
                event.player.location.z
            )
        )
        (pet as LivingEntity).setAI(false)

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
                        NamespacedKey(graysPets, "active-pet"),
                        PersistentDataType.STRING
                    )
                )
            ) == null
        ) {
            return
        }
        val pet = Bukkit.getEntity(
            UUID.fromString(
                owner.persistentDataContainer.get(
                    NamespacedKey(graysPets, "active-pet"),
                    PersistentDataType.STRING
                )
            )
        )!! as Mob
        if (!pet.persistentDataContainer.has(
                NamespacedKey(graysPets, "pet-pathfind"),
                PersistentDataType.INTEGER
            )
        ) {
            return
        }
        val loc = owner.location.clone()
        loc.x = owner.location.x + Math.random() * (4 - -4 + 1) + -4
        loc.z = owner.location.z + Math.random() * (4 - -4 + 1) + -4
        pet.teleport(loc)
        Bukkit.getScheduler().runTaskLater(
            graysPets,
            Runnable {
                (pet).persistentDataContainer.set(
                    NamespacedKey(graysPets, "pet-pathfind"),
                    PersistentDataType.INTEGER,
                    0
                )
            },
            10L
        )
        return
    }

    val hoppyLocations = mutableSetOf<Location>()

    @EventHandler
    fun onHoppyTheRabbitMove(event: EntityMoveEvent) {
        if (event.entityType != EntityType.RABBIT) {
            return
        }
        if (!event.entity.persistentDataContainer.has(
                NamespacedKey(graysPets, "pet-type"),
                PersistentDataType.STRING
            )
        ) {
            return
        }
        val pet = event.entity
        if (pet.persistentDataContainer.get(
                NamespacedKey(graysPets, "flower-cooldown"),
                PersistentDataType.INTEGER
            ) == 1
        ) {
            return
        }
        if (pet.persistentDataContainer.get(
                NamespacedKey(graysPets, "pet-type"),
                PersistentDataType.STRING
            ) == "hoppy"
        ) {
            if (event.entity.world.getBlockAt(event.entity.location).type != Material.AIR) {
                return
            }
            val loc = event.entity.location.block.location
            if (hoppyLocations.contains(loc)) {
                return
            }
            if (loc.world.getBlockAt(loc.clone().add(0.0, -1.0, 0.0)).type == Material.AIR) {
                return
            }
            hoppyLocations.add(loc)
            val packetPlayOutBlockChange = PacketPlayOutBlockChange(
                BlockPosition((CraftVector.toNMS(loc.toVector()))),
                Block.getByCombinedId((Math.random() * (1422 - 1412 + 1) + 1412).toInt())
            )
            val playersWhoSee = pet.location.getNearbyEntitiesByType(Player::class.java, 50.0)
            pet.persistentDataContainer.set(NamespacedKey(graysPets, "flower-cooldown"), PersistentDataType.INTEGER, 1)
            for (i in playersWhoSee) {
                (i as CraftPlayer).handle.playerConnection.sendPacket(packetPlayOutBlockChange)
            }
            Bukkit.getScheduler().runTaskLater(
                graysPets,
                Runnable {
                    pet.persistentDataContainer.set(
                        NamespacedKey(graysPets, "flower-cooldown"),
                        PersistentDataType.INTEGER,
                        0
                    )
                },
                10L
            )
            Bukkit.getScheduler().runTaskLater(graysPets, Runnable {
                if (event.entity.world.getBlockAt(loc).type == Material.AIR) {
                    val packetPlayOut = PacketPlayOutBlockChange(
                        BlockPosition((CraftVector.toNMS(loc.toVector()))),
                        Block.getByCombinedId(0)
                    )
                    for (i in playersWhoSee) {
                        (i as CraftPlayer).handle.playerConnection.sendPacket(packetPlayOut)
                    }
                    hoppyLocations.remove(loc)
                }
            }, 5 * 20L)
        }

    }

    @EventHandler
    fun onPlayerActivateVanityUltimateEvent(event: PlayerDropItemEvent) {
        if (event.itemDrop.itemStack.type != Material.DIAMOND) {
            return
        }
        if (!event.player.persistentDataContainer.has(
                NamespacedKey(graysPets, "active-pet"),
                PersistentDataType.STRING
            )
        ) {
            return
        }

        if (Bukkit.getEntity(
                UUID.fromString(
                    event.player.persistentDataContainer.get(
                        NamespacedKey(
                            graysPets,
                            "active-pet"
                        ), PersistentDataType.STRING
                    )
                )
            ) == null
        ) {
            return
        }
        val pet = Bukkit.getEntity(
            UUID.fromString(
                event.player.persistentDataContainer.get(
                    NamespacedKey(
                        graysPets,
                        "active-pet"
                    ), PersistentDataType.STRING
                )
            )
        )!! as Mob
        if (pet.persistentDataContainer.get(
                NamespacedKey(graysPets, "pet-cooldown"),
                PersistentDataType.INTEGER
            ) == 1
        ) {
            return
        }
        if (!pet.persistentDataContainer.has(NamespacedKey(graysPets, "pet-type"), PersistentDataType.STRING)) {
            return
        }
        event.isCancelled = true
        if (pet.persistentDataContainer.get(
                NamespacedKey(graysPets, "pet-type"),
                PersistentDataType.STRING
            ) == "kitsune"
        ) {
            (pet as Fox).velocity = Vector(0, 1, 0)
            val petY = pet.location.y + 5
            val dragon = EntityEnderDragon(EntityTypes.ENDER_DRAGON, (pet.world as CraftWorld).handle)
            val packetPlayOutEntityDestroy = PacketPlayOutEntityDestroy(dragon.id)
            val playersWhoSee = pet.location.getNearbyEntitiesByType(Player::class.java, 50.0)
            (pet).persistentDataContainer.set(NamespacedKey(graysPets, "pet-cooldown"), PersistentDataType.INTEGER, 1)
            Bukkit.getScheduler().runTaskLater(graysPets, Runnable {
                val loc = pet.location
                dragon.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.yaw, loc.yaw)
                val newLoc = pet.location
                newLoc.y = 1000.0
                pet.teleport(newLoc)
                val packetSpawn = PacketPlayOutSpawnEntityLiving(dragon)
                val packet = PacketPlayOutEntityStatus(dragon, 3.toByte())
                pet.world.spawnParticle(Particle.EXPLOSION_HUGE, pet.location, 10)
                pet.world.playSound(pet.location, Sound.ENTITY_ENDER_DRAGON_DEATH, 2.0f, 1.0f)
                pet.world.playSound(pet.location, Sound.ENTITY_ENDER_DRAGON_DEATH, 2.0f, 1.0f)
                pet.setAI(false)
                for (i in playersWhoSee) {
                    (i as CraftPlayer).handle.playerConnection.sendPacket(packetSpawn)
                    i.handle.playerConnection.sendPacket(packet)
                    i.playSound(pet.location, Sound.ENTITY_ENDER_DRAGON_DEATH, 2.0f, 1.0f)
                }
                pet.isInvisible = true
            }, 10L)
            Bukkit.getScheduler().runTaskLater(graysPets, Runnable {
                (pet).persistentDataContainer.set(
                    NamespacedKey(graysPets, "pet-pathfind"),
                    PersistentDataType.INTEGER,
                    0
                )

                pet.teleport(Location(pet.location.world, pet.location.x, petY, pet.location.z))
                for (i in playersWhoSee) {
                    (i as CraftPlayer).handle.playerConnection.sendPacket(packetPlayOutEntityDestroy)
                }
                pet.isInvisible = false
                pet.setAI(true)
            }, 220L)
            Bukkit.getScheduler().runTaskLater(
                graysPets,
                Runnable {
                    (pet).persistentDataContainer.set(
                        NamespacedKey(graysPets, "pet-cooldown"),
                        PersistentDataType.INTEGER,
                        0
                    )
                },
                240L
            )

        }
        if (pet.persistentDataContainer.get(
                NamespacedKey(graysPets, "pet-type"),
                PersistentDataType.STRING
            ) == "chelsea"
        ) {
            val nearestEntityList =
                pet.location.getNearbyEntities(pet.location.x + 25, pet.location.y + 25, pet.location.z + 25)
                    .toMutableList()
            val acceptableEntities = mutableListOf<LivingEntity>()
            for (i in nearestEntityList) {
                if (i.type == EntityType.OCELOT) {
                    continue
                }
                if (i.type == EntityType.PLAYER) {
                    continue
                }
                if (i !is LivingEntity) {
                    continue
                }
                acceptableEntities.add(i)
            }
            if (acceptableEntities.isEmpty()) {
                return
            }
            val nearestEntity = acceptableEntities[0]
            val yaw = Math.toDegrees(
                Math.atan2(
                    nearestEntity.location.z - pet.location.z, nearestEntity.location.x - pet.location.x
                )
            ).toFloat() - 90
            pet.persistentDataContainer.set(NamespacedKey(graysPets, "pet-cooldown"), PersistentDataType.INTEGER, 1)
            val loc = pet.location.clone()
            loc.yaw = yaw
            pet.teleport(loc)
            pet.velocity = Vector(
                nearestEntity.location.x - pet.location.x,
                nearestEntity.location.y - pet.location.y,
                nearestEntity.location.z - pet.location.z
            )
            val arrow = pet.world.spawnArrow(pet.location, loc.direction, 1.0f, 0.0f)
            arrow.velocity = Vector(
                nearestEntity.location.x - pet.location.x,
                nearestEntity.location.y - pet.location.y,
                nearestEntity.location.z - pet.location.z
            )
            arrow.damage = 0.0
            arrow.persistentDataContainer.set(
                NamespacedKey(graysPets, "chelsea-uuid"),
                PersistentDataType.STRING,
                pet.uniqueId.toString()

            )
            Bukkit.getScheduler().runTaskLater(
                graysPets,
                Runnable {
                    (pet).persistentDataContainer.set(
                        NamespacedKey(graysPets, "pet-cooldown"),
                        PersistentDataType.INTEGER,
                        0
                    )
                },
                240L
            )
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
                        NamespacedKey(graysPets, "active-pet"),
                        PersistentDataType.STRING
                    )
                )
            ) == null
        ) {
            return
        }
        val pet = Bukkit.getEntity(
            UUID.fromString(
                owner.persistentDataContainer.get(
                    NamespacedKey(graysPets, "active-pet"),
                    PersistentDataType.STRING
                )
            )
        )!! as Mob
        if (!pet.persistentDataContainer.has(
                NamespacedKey(graysPets, "pet-pathfind"),
                PersistentDataType.INTEGER
            ) || pet.persistentDataContainer.get(
                NamespacedKey(graysPets, "pet-pathfind"),
                PersistentDataType.INTEGER
            ) == 1
        ) {
            return
        }
        val loc = owner.location.clone()
        loc.x = owner.location.x + Math.random() * (4 - -4 + 1) + -4
        loc.z = owner.location.z + Math.random() * (4 - -4 + 1) + -4
        pet.teleport(loc)
        Bukkit.getScheduler().runTaskLater(
            graysPets,
            Runnable {
                (pet).persistentDataContainer.set(
                    NamespacedKey(graysPets, "pet-pathfind"),
                    PersistentDataType.INTEGER,
                    0
                )
            },
            10L
        )
        return

    }

    @EventHandler
    fun entityCollideEvent(event: ProjectileCollideEvent) {
        if (event.entity.type != EntityType.ARROW) {
            return
        }
        if (!event.entity.persistentDataContainer.has(
                NamespacedKey(graysPets, "chelsea-uuid"),
                PersistentDataType.STRING
            )
        ) {
            return
        }
        val pet = Bukkit.getEntity(
            UUID.fromString(
                event.entity.persistentDataContainer.get(
                    NamespacedKey(
                        graysPets,
                        "chelsea-uuid"
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
                    event.collidedWith.location,
                    Sound.ENTITY_LIGHTNING_BOLT_THUNDER,
                    2.0f,
                    1.0f
                )
                i.world.spawnParticle(Particle.EXPLOSION_HUGE, loc, 2)
            }, 2, 0)

            Bukkit.getScheduler()
                .runTaskLater(graysPets, Runnable { Bukkit.getScheduler().cancelTask(task.taskId) }, 40L)
        }
    }

}