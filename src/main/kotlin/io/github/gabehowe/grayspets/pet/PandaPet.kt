package io.github.gabehowe.grayspets.pet

import io.github.gabehowe.grayspets.BasePet
import io.github.gabehowe.grayspets.GraysPets
import io.github.gabehowe.grayspets.PetFactory
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Panda
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

class PandaPet(entity: Entity, graysPets: GraysPets) : BasePet(entity, graysPets) {
    companion object {
    fun create(gene: Panda.Gene, petType: PetFactory.PetType, graysPets: GraysPets, player: Player, isBaby: Boolean): Entity {
        val entity = createEntity(graysPets, EntityType.PANDA, player, isBaby)
        entity as Panda
        entity.hiddenGene = gene
        entity.mainGene = gene
        entity.persistentDataContainer.set(graysPets.petTypeKey, PersistentDataType.STRING, "$petType")
        return entity
    }
}

}