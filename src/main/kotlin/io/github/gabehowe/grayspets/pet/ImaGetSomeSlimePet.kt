package io.github.gabehowe.grayspets.pet

import io.github.gabehowe.grayspets.BasePet
import io.github.gabehowe.grayspets.GraysPets
import io.github.gabehowe.grayspets.PetFactory
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

class ImaGetSomeSlimePet(entity: Entity, graysPets: GraysPets) : BasePet(entity, graysPets) {
    companion object {

        fun create(petType: PetFactory.PetType, entityType: EntityType, graysPets: GraysPets, player: Player, isBaby: Boolean): Entity {
            val entity = createEntity(graysPets, entityType, player, isBaby)
            entity.persistentDataContainer.set(graysPets.petTypeKey, PersistentDataType.STRING, "$petType")
            return entity
        }
    }
}