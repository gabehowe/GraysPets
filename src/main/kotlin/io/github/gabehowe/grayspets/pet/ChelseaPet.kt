package io.github.gabehowe.grayspets.pet

import io.github.gabehowe.grayspets.BasePet
import io.github.gabehowe.grayspets.GraysPets
import io.github.gabehowe.grayspets.PetFactory
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.*
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import java.lang.NullPointerException
import kotlin.math.atan2

class ChelseaPet(entity: Entity, graysPets: GraysPets) : BasePet(entity, graysPets), VanityUltimate {

    override fun cleanup() {
        super.cleanup()
        entity.persistentDataContainer.set(graysPets.petCooldownKey, PersistentDataType.INTEGER, 0)
    }

    override fun activateUltimate() {
        val nearestEntityList = entity.location.getNearbyEntities(entity.location.x + 25, entity.location.y + 25, entity.location.z + 25)
            .toMutableList()
        val acceptableEntities = mutableListOf<LivingEntity>()
        for (i in nearestEntityList) {
            if (i.type == EntityType.PANDA) {
                continue
            }
            if (i.type == EntityType.PLAYER) {
                continue
            }
            if (i !is LivingEntity) {
                continue
            }
            if (i.location.y != entity.location.y) {
                continue
            }
            acceptableEntities.add(i)
        }
        if (acceptableEntities.isEmpty()) {
            return
        }
        val nearestEntity = acceptableEntities[0]
        if (nearestEntity.location.y != entity.location.y) {
            return
        }
        entity.persistentDataContainer.set(NamespacedKey(graysPets, "pet-cooldown"), PersistentDataType.INTEGER, 1)
        val yaw = Math.toDegrees(
            atan2(
                nearestEntity.location.z - entity.location.z, nearestEntity.location.x - entity.location.x
            )
        ).toFloat() - 90
        val loc = entity.location.clone()
        loc.yaw = yaw
        entity.teleport(loc)
        entity.velocity = Vector(
            nearestEntity.location.x - entity.location.x, nearestEntity.location.y - entity.location.y, nearestEntity.location.z - entity.location.z
        )
        val arrowLoc = entity.location.clone()
        arrowLoc.y += 1
        val arrow = entity.world.spawnArrow(arrowLoc, loc.direction, 1.0f, 0.0f)
        arrow.velocity = Vector(
            nearestEntity.location.x - entity.location.x, nearestEntity.location.y - entity.location.y, nearestEntity.location.z - entity.location.z
        )
        arrow.damage = 0.0
        arrow.persistentDataContainer.set(
            NamespacedKey(graysPets, "chelsea-uuid"), PersistentDataType.STRING, entity.uniqueId.toString()

        )
        Bukkit.getScheduler().runTaskLater(
            graysPets, Runnable {
                (entity).persistentDataContainer.set(
                    NamespacedKey(graysPets, "pet-cooldown"), PersistentDataType.INTEGER, 0
                )
                try {
                 arrow.remove()
                }
                catch (e : NullPointerException) {

                }
            }, 240L
        )
    }

    companion object {

        fun create(petType: PetFactory.PetType, graysPets: GraysPets, player: Player, isBaby: Boolean): Entity {
            val entity = createEntity(graysPets, EntityType.PANDA, player, isBaby)
            entity as Panda
            entity.customName = "§bChelsea"
            entity.mainGene = Panda.Gene.BROWN
            entity.hiddenGene = Panda.Gene.BROWN
            entity.persistentDataContainer.set(graysPets.petCooldownKey, PersistentDataType.INTEGER, 0)
            entity.persistentDataContainer.set(graysPets.petTypeKey, PersistentDataType.STRING, "$petType")
            return entity
        }
    }
}