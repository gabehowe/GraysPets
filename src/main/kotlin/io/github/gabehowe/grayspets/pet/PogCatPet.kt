package io.github.gabehowe.grayspets.pet

import io.github.gabehowe.grayspets.BasePet
import io.github.gabehowe.grayspets.GraysPets
import io.github.gabehowe.grayspets.PetFactory
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Ocelot
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

class PogCatPet(entity: Entity, graysPets: GraysPets) : BasePet(entity, graysPets) {
    companion object {
    fun create(petType: PetFactory.PetType, graysPets: GraysPets, player: Player, isBaby: Boolean): Entity {
        val ent = createEntity(graysPets, EntityType.OCELOT, player, isBaby)
        ent as Ocelot
        ent.customName = "pog cat"
        ent.persistentDataContainer.set(graysPets.petTypeKey, PersistentDataType.STRING, "$petType")
        return ent
    }
}


}