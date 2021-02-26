package io.github.gabehowe.grayspets.pet

import io.github.gabehowe.grayspets.BasePet
import io.github.gabehowe.grayspets.GraysPets
import io.github.gabehowe.grayspets.PetFactory
import net.minecraft.server.v1_16_R3.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_16_R3.util.CraftVector
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Fox
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


class PolemistisPet(entity: Entity, graysPets: GraysPets) : BasePet(entity, graysPets), VanityUltimate {


    override fun cleanup() {
        super.cleanup()
        entity.persistentDataContainer.set(graysPets.petCooldownKey, PersistentDataType.INTEGER, 0)
    }

    companion object {

        fun create(petType: PetFactory.PetType, graysPets: GraysPets, player: Player, isBaby: Boolean): Entity {
            val ent = createEntity(graysPets, EntityType.FOX, player, isBaby)
            ent.isGlowing = true
            ent.customName = "§2πολεμ§aιστής"
            ent as Fox
            ent.foxType = Fox.Type.SNOW
            ent.firstTrustedPlayer = player
            ent.persistentDataContainer.set(graysPets.petTypeKey, PersistentDataType.STRING, "$petType")
            ent.persistentDataContainer.set(graysPets.petCooldownKey, PersistentDataType.INTEGER, 0)
            return ent
        }
    }

    override fun activateUltimate() { //mid is the middle location for the circle
        val mid = entity.location
        val particles = 10
        val radius = 5
        val packetEntityList = mutableListOf<EntityFox>()
        entity.persistentDataContainer.set(graysPets.petCooldownKey, PersistentDataType.INTEGER, 1)
        for (i in 0 until particles) {
            val angle: Double = 2 * Math.PI * i / particles
            val x: Double = cos(angle) * radius
            val y: Double = sin(angle) * radius
            var v: Vector? = rotateAroundAxisX(Vector(x, y, 0.0), Location(null, 0.0, 0.0, 0.0, 0.0f, 90.0f).pitch.toDouble())
            v = rotateAroundAxisY(v!!, Location(null, 0.0, 0.0, 0.0, 0.0f, 90.0f).getYaw().toDouble());
            val temp = mid.clone().add(v) //spawn particles at location temp using any method you like
            val packetEntity = EntityFox(EntityTypes.FOX, (entity.world as CraftWorld).handle)
            val yaw = Math.toDegrees(
                atan2(
                    entity.location.z - temp.z, entity.location.x - temp.x
                )
            ) + 90f
            packetEntity.isNoAI = true
            packetEntity.isInvulnerable = true
            packetEntity.i(false)
            entity.world.spawnParticle(Particle.EXPLOSION_HUGE, entity.location, 2)
            entity.world.playSound(entity.location, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 1.0f)
            packetEntity.mot = Vec3D(
                entity.location.x - temp.x, 3.0, entity.location.z - temp.z
            )
            val rand = (Math.random() * (3 - 1 + 1) + 1).toInt()
            if (rand in 1..2) {
                packetEntity.foxType = EntityFox.Type.SNOW
            } else {
                packetEntity.foxType = EntityFox.Type.RED
            } // entity.setPositionRotation(temp.x,temp.y,temp.z, temp.pitch, yaw.toFloat())
            packetEntity.setLocation(temp.x, temp.y, temp.z, 0.0f, 0.0f)
            packetEntityList.add(packetEntity)
            val packetSpawn = PacketPlayOutSpawnEntityLiving(packetEntity)
            val packet = PacketPlayOutEntityMetadata(packetEntity.id, packetEntity.dataWatcher, false)
            val entityRotation = PacketPlayOutEntityHeadRotation(packetEntity, (((yaw) * 256.0f / 360.0f).toInt()).toByte())
            for (e in Bukkit.getOnlinePlayers()) {
                (e as CraftPlayer).handle.playerConnection.sendPacket(packetSpawn)
                e.handle.playerConnection.sendPacket(packet)
                e.handle.playerConnection.sendPacket(entityRotation)
            }


        }
        var posMap = mutableMapOf<EntityFox, Vector>()
        for (i in packetEntityList) {
            posMap[i] = CraftVector.toBukkit(i.positionVector)
        }
        val task = Bukkit.getScheduler().scheduleSyncRepeatingTask(
            graysPets, {
                for (packetEntity in packetEntityList) {
                    if (posMap[packetEntity] == null) {
                        continue
                    }
                    val vec = CraftVector.toBukkit(packetEntity.mot).multiply(0.9).subtract(Vector(0.0, 0.49, 0.0))
                    val lastPos = packetEntity.positionVector
                    posMap[packetEntity] = CraftVector.toBukkit(packetEntity.positionVector).add(vec)
                    val pos = posMap[packetEntity]!!
                    packetEntity.mot = CraftVector.toNMS(vec)
                    packetEntity.setPosition(pos.x, pos.y, pos.z)
                    val veloPacket = PacketPlayOutEntityVelocity(packetEntity)
                    val posPacket = PacketPlayOutEntity.PacketPlayOutRelEntityMove(
                        packetEntity.id, ((pos.x * 32 - lastPos.x * 32) * 128).toInt()
                            .toShort(), ((pos.y * 32 - lastPos.y * 32) * 128).toInt()
                            .toShort(), ((pos.z * 32 - lastPos.z * 32) * 128).toInt().toShort(), false
                    )
                    if (Location(entity.world, lastPos.x, lastPos.y, lastPos.z).block.type.isSolid) {
                        val loc = Location(entity.world, pos.x, pos.y, pos.z)
                        packetEntity.setLocation(loc.x, loc.y, loc.z, packetEntity.yaw, packetEntity.pitch)
                        entity.world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f)
                        entity.world.playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2.0f, 1.0f)
                        entity.world.spawnParticle(Particle.EXPLOSION_HUGE, loc, 5)
                        val destroyPacket = PacketPlayOutEntityDestroy(packetEntity.id)
                        val lightning = EntityLightning(EntityTypes.LIGHTNING_BOLT, (entity.world as CraftWorld).handle)
                        lightning.setLocation(pos.x, pos.y, pos.z, 0.0f, 0.0f)
                        val lightningPacket = PacketPlayOutSpawnEntity(lightning)
                        packetEntity.mot = Vec3D(0.0, 0.0, 0.0)
                        for (i in Bukkit.getOnlinePlayers()) {
                            (i as CraftPlayer).handle.playerConnection.sendPacket(destroyPacket)
                            i.handle.playerConnection.sendPacket(lightningPacket)
                        }
                        posMap.remove(packetEntity)
                    }
                    for (i in Bukkit.getOnlinePlayers()) {
                        (i as CraftPlayer).handle.playerConnection.sendPacket(posPacket)
                        i.handle.playerConnection.sendPacket(veloPacket)
                    }
                }
            }, 1L, 2L
        )
        val cooldownResetTask = Bukkit.getScheduler()
            .runTaskLater(graysPets, Runnable { entity.persistentDataContainer.set(graysPets.petCooldownKey, PersistentDataType.INTEGER, 0) }, 100L)
        val cancelTask = Bukkit.getScheduler().scheduleSyncDelayedTask(graysPets, {
            Bukkit.getScheduler().cancelTask(task)
            for (i in packetEntityList) {
                if (posMap[i] == null) {
                    continue
                }
                val pos = posMap[i]!!
                val loc = Location(entity.world, pos.x, pos.y, pos.z)
                val lightning = EntityLightning(EntityTypes.LIGHTNING_BOLT, (entity.world as CraftWorld).handle)
                lightning.setLocation(pos.x, pos.y, pos.z, 0.0f, 0.0f)
                val lightningPacket = PacketPlayOutSpawnEntity(lightning)
                for (e in Bukkit.getOnlinePlayers()) {
                    (e as CraftPlayer).handle.playerConnection.sendPacket(lightningPacket)
                }
                entity.world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f)
                entity.world.playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2.0f, 1.0f)
                entity.world.spawnParticle(Particle.EXPLOSION_HUGE, loc, 5)
            }
            val destroyPacket = PacketPlayOutEntityDestroy(*packetEntityList.map { it.id }.toTypedArray().toIntArray())
            for (i in Bukkit.getOnlinePlayers()) {
                (i as CraftPlayer).handle.playerConnection.sendPacket(destroyPacket)
            }


        }, 20L)
    }

    private fun rotateAroundAxisX(v: Vector, angle: Double): Vector {
        var ang = angle
        ang = Math.toRadians(ang)
        val y: Double
        val z: Double
        val cos: Double = cos(ang)
        val sin: Double = Math.sin(ang)
        y = v.y * cos - v.z * sin
        z = v.y * sin + v.z * cos
        return v.setY(y).setZ(z)
    }

    private fun rotateAroundAxisY(v: Vector, angle: Double): Vector {
        var ang = angle
        ang = -ang
        ang = Math.toRadians(ang)
        val x: Double
        val z: Double
        val cos: Double
        val sin: Double
        cos = Math.cos(ang)
        sin = Math.sin(ang)
        x = v.x * cos + v.z * sin
        z = v.x * -sin + v.z * cos
        return v.setX(x).setZ(z)
    }


}