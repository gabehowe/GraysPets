package io.github.gabehowe.grayspets.pet

import io.github.gabehowe.grayspets.BasePet
import io.github.gabehowe.grayspets.GraysPets
import io.github.gabehowe.grayspets.PetFactory
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Fox
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

class FoxPet(entity: Entity, graysPets: GraysPets, isHidden : Boolean) :
    BasePet(entity, graysPets, isHidden) {


    companion object {

        fun create(foxType: Fox.Type, petType: PetFactory.PetType, graysPets: GraysPets, player: Player, isBaby: Boolean): Entity {
            val entity = createEntity(graysPets, EntityType.FOX, player, isBaby)
            entity as Fox
            entity.foxType = foxType
            entity.persistentDataContainer.set(graysPets.petTypeKey, PersistentDataType.STRING, "$petType")
            player.persistentDataContainer.set(graysPets.petTypeKey, PersistentDataType.STRING, "$petType")
            return entity
        }
    }
}