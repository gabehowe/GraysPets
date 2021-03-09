package io.github.gabehowe.grayspets

import io.github.gabehowe.grayspets.pet.*
import org.bukkit.entity.*

/*
* fish
* right
* to
* where
* are
* */
class PetFactory {

    companion object {

        fun makePet(petType: PetType, graysPets: GraysPets, player: Player): BasePet {
            val pet = when (petType) {
                PetType.POLEMISTIS -> PolemistisPet(PolemistisPet.create(petType, graysPets, player, false), graysPets, false)
                PetType.BABY_POLEMISTIS -> PolemistisPet(PolemistisPet.create(petType, graysPets, player, true), graysPets, false)
                PetType.BABY_BROWN_PANDA -> PandaPet(PandaPet.create(Panda.Gene.BROWN, petType, graysPets, player, true), graysPets, false)
                PetType.ARCTIC_FOX -> FoxPet(FoxPet.create(Fox.Type.SNOW, petType, graysPets, player, false), graysPets, false)
                PetType.BABY_ARCTIC_FOX -> FoxPet(FoxPet.create(Fox.Type.SNOW, petType, graysPets, player, true), graysPets, false)
                PetType.CHELSEA -> ChelseaPet(ChelseaPet.create(petType, graysPets, player, true), graysPets, false)
                PetType.KITSUNE -> KitsunePet(KitsunePet.create(petType, graysPets, player, false), graysPets, false)
                PetType.BABY_KITSUNE -> KitsunePet(KitsunePet.create(petType, graysPets, player, true), graysPets, false)
                PetType.HOPPY -> HoppyPet(HoppyPet.create(petType, graysPets, player, false), graysPets, false)
                PetType.BABY_HOPPY -> HoppyPet(HoppyPet.create(petType, graysPets, player, true), graysPets, false)
                PetType.POG_CAT -> PogCatPet(PogCatPet.create(petType, graysPets, player, false), graysPets, false)
                PetType.BABY_POG_CAT -> PogCatPet(PogCatPet.create(petType, graysPets, player, true), graysPets, false)
                PetType.NICK -> NickPet(NickPet.create(petType, graysPets, player, false), graysPets, false)
                PetType.BABY_NICK -> NickPet(NickPet.create(petType, graysPets, player, true), graysPets, false)
                PetType.BABY_CHICKEN -> SimplePet(SimplePet.create(petType, EntityType.CHICKEN, graysPets, player, true), graysPets, false)
                PetType.BABY_COW -> SimplePet(SimplePet.create(petType, EntityType.COW, graysPets, player, true), graysPets, false)
                PetType.BABY_DONKEY -> SimplePet(SimplePet.create(petType, EntityType.DONKEY, graysPets, player, true), graysPets, false)
                PetType.BABY_HORSE -> SimplePet(SimplePet.create(petType, EntityType.HORSE, graysPets, player, true), graysPets, false)
                PetType.BABY_MOOSHROOM -> SimplePet(SimplePet.create(petType, EntityType.MUSHROOM_COW, graysPets, player, true), graysPets, false)
                PetType.BABY_OCELOT -> SimplePet(SimplePet.create(petType, EntityType.OCELOT, graysPets, player, true), graysPets, false)
                PetType.BABY_FOX -> FoxPet(FoxPet.create(Fox.Type.RED, petType, graysPets, player, true), graysPets, false)
                PetType.BABY_BEE -> SimplePet(SimplePet.create(petType, EntityType.BEE, graysPets, player, true), graysPets, false)
                PetType.BABY_BLACK_PANDA -> PandaPet(PandaPet.create(Panda.Gene.NORMAL, petType, graysPets, player, true), graysPets, false)
                PetType.BABY_PIG -> SimplePet(SimplePet.create(petType, EntityType.PIG, graysPets, player, true), graysPets, false)
                PetType.BABY_BEAR -> SimplePet(SimplePet.create(petType, EntityType.POLAR_BEAR, graysPets, player, true), graysPets, false)
                PetType.BABY_RABBIT -> SimplePet(SimplePet.create(petType, EntityType.RABBIT, graysPets, player, true), graysPets, false)
                PetType.BABY_SHEEP -> SimplePet(SimplePet.create(petType, EntityType.SHEEP, graysPets, player, true), graysPets, false)
                PetType.BABY_TURTLE -> SimplePet(SimplePet.create(petType, EntityType.TURTLE, graysPets, player, true), graysPets, false)
                PetType.CHICKEN -> SimplePet(SimplePet.create(petType, EntityType.CHICKEN, graysPets, player, false), graysPets, false)
                PetType.PIG -> SimplePet(SimplePet.create(petType, EntityType.PIG, graysPets, player, false), graysPets, false)
                PetType.RABBIT -> SimplePet(SimplePet.create(petType, EntityType.RABBIT, graysPets, player, false), graysPets, false)
                PetType.TURTLE -> SimplePet(SimplePet.create(petType, EntityType.TURTLE, graysPets, player, false), graysPets, false)
                PetType.BEE -> SimplePet(SimplePet.create(petType, EntityType.BEE, graysPets, player, false), graysPets, false)
                PetType.FOX -> FoxPet(FoxPet.create(Fox.Type.RED, petType, graysPets, player, false), graysPets, false)
                PetType.OCELOT -> SimplePet(SimplePet.create(petType, EntityType.OCELOT, graysPets, player, false), graysPets, false)
            }
            graysPets.petMap[player.uniqueId] = pet
            return pet
        }

        fun loadPet(petType: PetType, entity: Entity, graysPets: GraysPets, isHidden : Boolean): BasePet {
            val pet = when (petType) {
                PetType.POLEMISTIS -> PolemistisPet(entity, graysPets, isHidden)
                PetType.BABY_POLEMISTIS -> PolemistisPet(entity, graysPets, isHidden)
                PetType.BABY_BROWN_PANDA -> PandaPet(entity, graysPets, isHidden)
                PetType.ARCTIC_FOX -> FoxPet(entity, graysPets, isHidden)
                PetType.BABY_ARCTIC_FOX -> FoxPet(entity, graysPets, isHidden)
                PetType.CHELSEA -> ChelseaPet(entity, graysPets, isHidden)
                PetType.KITSUNE -> KitsunePet(entity, graysPets, isHidden)
                PetType.BABY_KITSUNE -> KitsunePet(entity, graysPets, isHidden)
                PetType.HOPPY -> HoppyPet(entity, graysPets, isHidden)
                PetType.BABY_HOPPY -> HoppyPet(entity, graysPets, isHidden)
                PetType.POG_CAT -> PogCatPet(entity, graysPets, isHidden)
                PetType.BABY_POG_CAT -> PogCatPet(entity, graysPets, isHidden)
                PetType.NICK -> NickPet(entity, graysPets, isHidden)
                PetType.BABY_NICK -> NickPet(entity, graysPets, isHidden)
                PetType.BABY_CHICKEN -> SimplePet(entity, graysPets, isHidden)
                PetType.BABY_COW -> SimplePet(entity, graysPets, isHidden)
                PetType.BABY_DONKEY -> SimplePet(entity, graysPets, isHidden)
                PetType.BABY_HORSE -> SimplePet(entity, graysPets, isHidden)
                PetType.BABY_MOOSHROOM -> SimplePet(entity, graysPets, isHidden)
                PetType.BABY_OCELOT -> SimplePet(entity, graysPets, isHidden)
                PetType.BABY_FOX -> FoxPet(entity, graysPets, isHidden)
                PetType.BABY_BEE -> SimplePet(entity, graysPets, isHidden)
                PetType.BABY_BLACK_PANDA -> PandaPet(entity, graysPets, isHidden)
                PetType.BABY_PIG -> SimplePet(entity, graysPets, isHidden)
                PetType.BABY_BEAR -> SimplePet(entity, graysPets, isHidden)
                PetType.BABY_RABBIT -> SimplePet(entity, graysPets, isHidden)
                PetType.BABY_SHEEP -> SimplePet(entity, graysPets, isHidden)
                PetType.BABY_TURTLE -> SimplePet(entity, graysPets, isHidden)
                PetType.CHICKEN -> SimplePet(entity, graysPets, isHidden)
                PetType.PIG -> SimplePet(entity, graysPets, isHidden)
                PetType.RABBIT -> SimplePet(entity, graysPets, isHidden)
                PetType.TURTLE -> SimplePet(entity, graysPets, isHidden)
                PetType.BEE -> SimplePet(entity, graysPets, isHidden)
                PetType.FOX -> FoxPet(entity, graysPets, isHidden)
                PetType.OCELOT -> SimplePet(entity, graysPets, isHidden)
            }
            graysPets.petMap[BasePet.getOwnerUUID(graysPets, entity)] = pet
            return pet
        }
    }

    enum class PetType { ARCTIC_FOX, BABY_ARCTIC_FOX, BABY_BEAR, BABY_BEE, BABY_BLACK_PANDA, BABY_BROWN_PANDA, BABY_CHICKEN, BABY_COW, BABY_DONKEY, BABY_FOX, BABY_HOPPY, BABY_HORSE, BABY_KITSUNE, BABY_MOOSHROOM, BABY_NICK, BABY_OCELOT, BABY_PIG, BABY_POG_CAT, BABY_POLEMISTIS, BABY_RABBIT, BABY_SHEEP, BABY_TURTLE, BEE, CHELSEA, CHICKEN, FOX, HOPPY, KITSUNE, NICK, OCELOT, PIG, POG_CAT, POLEMISTIS, RABBIT, TURTLE
    }
}
