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
                PetType.POLEMISTIS -> PolemistisPet(PolemistisPet.create(petType, graysPets, player, false), graysPets)
                PetType.BABY_POLEMISTIS -> PolemistisPet(PolemistisPet.create(petType, graysPets, player, true), graysPets)
                PetType.BABY_BROWN_PANDA -> PandaPet(PandaPet.create(Panda.Gene.BROWN, petType, graysPets, player, true), graysPets)
                PetType.ARCTIC_FOX -> FoxPet(FoxPet.create(Fox.Type.SNOW, petType, graysPets, player, false), graysPets)
                PetType.BABY_ARCTIC_FOX -> FoxPet(FoxPet.create(Fox.Type.SNOW, petType, graysPets, player, true), graysPets)
                PetType.CHELSEA -> ChelseaPet(ChelseaPet.create(petType, graysPets, player, true), graysPets)
                PetType.KITSUNE -> KitsunePet(KitsunePet.create(petType, graysPets, player, false), graysPets)
                PetType.BABY_KITSUNE -> KitsunePet(KitsunePet.create(petType, graysPets, player, true), graysPets)
                PetType.HOPPY -> HoppyPet(HoppyPet.create(petType, graysPets, player, false), graysPets)
                PetType.BABY_HOPPY -> HoppyPet(HoppyPet.create(petType, graysPets, player, true), graysPets)
                PetType.POG_CAT -> PogCatPet(PogCatPet.create(petType, graysPets, player, false), graysPets)
                PetType.BABY_POG_CAT -> PogCatPet(PogCatPet.create(petType, graysPets, player, true), graysPets)
                PetType.NICK -> NickPet(NickPet.create(petType, graysPets, player, false), graysPets)
                PetType.BABY_NICK -> NickPet(NickPet.create(petType, graysPets, player, true), graysPets)
                PetType.BABY_CHICKEN -> SimplePet(SimplePet.create(petType, EntityType.CHICKEN, graysPets, player, true), graysPets)
                PetType.BABY_COW -> SimplePet(SimplePet.create(petType, EntityType.COW, graysPets, player, true), graysPets)
                PetType.BABY_DONKEY -> SimplePet(SimplePet.create(petType, EntityType.DONKEY, graysPets, player, true), graysPets)
                PetType.BABY_HORSE -> SimplePet(SimplePet.create(petType, EntityType.HORSE, graysPets, player, true), graysPets)
                PetType.BABY_MOOSHROOM -> SimplePet(SimplePet.create(petType, EntityType.MUSHROOM_COW, graysPets, player, true), graysPets)
                PetType.BABY_OCELOT -> SimplePet(SimplePet.create(petType, EntityType.OCELOT, graysPets, player, true), graysPets)
                PetType.BABY_FOX -> FoxPet(FoxPet.create(Fox.Type.RED, petType, graysPets, player, true), graysPets)
                PetType.BABY_BEE -> SimplePet(SimplePet.create(petType, EntityType.BEE, graysPets, player, true), graysPets)
                PetType.BABY_BLACK_PANDA -> PandaPet(PandaPet.create(Panda.Gene.NORMAL, petType, graysPets, player, true), graysPets)
                PetType.BABY_PIG -> SimplePet(SimplePet.create(petType, EntityType.PIG, graysPets, player, true), graysPets)
                PetType.BABY_BEAR -> SimplePet(SimplePet.create(petType, EntityType.POLAR_BEAR, graysPets, player, true), graysPets)
                PetType.BABY_RABBIT -> SimplePet(SimplePet.create(petType, EntityType.RABBIT, graysPets, player, true), graysPets)
                PetType.BABY_SHEEP -> SimplePet(SimplePet.create(petType, EntityType.SHEEP, graysPets, player, true), graysPets)
                PetType.BABY_TURTLE -> SimplePet(SimplePet.create(petType, EntityType.TURTLE, graysPets, player, true), graysPets)
                PetType.CHICKEN -> SimplePet(SimplePet.create(petType, EntityType.CHICKEN, graysPets, player, false), graysPets)
                PetType.PIG -> SimplePet(SimplePet.create(petType, EntityType.PIG, graysPets, player, false), graysPets)
                PetType.RABBIT -> SimplePet(SimplePet.create(petType, EntityType.RABBIT, graysPets, player, false), graysPets)
                PetType.TURTLE -> SimplePet(SimplePet.create(petType, EntityType.TURTLE, graysPets, player, false), graysPets)
                PetType.BEE -> SimplePet(SimplePet.create(petType, EntityType.BEE, graysPets, player, false), graysPets)
                PetType.FOX -> FoxPet(FoxPet.create(Fox.Type.RED, petType, graysPets, player, false), graysPets)
                PetType.OCELOT -> SimplePet(SimplePet.create(petType, EntityType.OCELOT, graysPets, player, false), graysPets)
            }
            graysPets.petMap[player.uniqueId] = pet
            return pet
        }

        fun loadPet(petType: PetType, entity: Entity, graysPets: GraysPets): BasePet {
            val pet = when (petType) {
                PetType.POLEMISTIS -> PolemistisPet(entity, graysPets)
                PetType.BABY_POLEMISTIS -> PolemistisPet(entity, graysPets)
                PetType.BABY_BROWN_PANDA -> PandaPet(entity, graysPets)
                PetType.ARCTIC_FOX -> FoxPet(entity, graysPets)
                PetType.BABY_ARCTIC_FOX -> FoxPet(entity, graysPets)
                PetType.CHELSEA -> ChelseaPet(entity, graysPets)
                PetType.KITSUNE -> KitsunePet(entity, graysPets)
                PetType.BABY_KITSUNE -> KitsunePet(entity, graysPets)
                PetType.HOPPY -> HoppyPet(entity, graysPets)
                PetType.BABY_HOPPY -> HoppyPet(entity, graysPets)
                PetType.POG_CAT -> PogCatPet(entity, graysPets)
                PetType.BABY_POG_CAT -> PogCatPet(entity, graysPets)
                PetType.NICK -> NickPet(entity, graysPets)
                PetType.BABY_NICK -> NickPet(entity, graysPets)
                PetType.BABY_CHICKEN -> SimplePet(entity, graysPets)
                PetType.BABY_COW -> SimplePet(entity, graysPets)
                PetType.BABY_DONKEY -> SimplePet(entity, graysPets)
                PetType.BABY_HORSE -> SimplePet(entity, graysPets)
                PetType.BABY_MOOSHROOM -> SimplePet(entity, graysPets)
                PetType.BABY_OCELOT -> SimplePet(entity, graysPets)
                PetType.BABY_FOX -> FoxPet(entity, graysPets)
                PetType.BABY_BEE -> SimplePet(entity, graysPets)
                PetType.BABY_BLACK_PANDA -> PandaPet(entity, graysPets)
                PetType.BABY_PIG -> SimplePet(entity, graysPets)
                PetType.BABY_BEAR -> SimplePet(entity, graysPets)
                PetType.BABY_RABBIT -> SimplePet(entity, graysPets)
                PetType.BABY_SHEEP -> SimplePet(entity, graysPets)
                PetType.BABY_TURTLE -> SimplePet(entity, graysPets)
                PetType.CHICKEN -> SimplePet(entity, graysPets)
                PetType.PIG -> SimplePet(entity, graysPets)
                PetType.RABBIT -> SimplePet(entity, graysPets)
                PetType.TURTLE -> SimplePet(entity, graysPets)
                PetType.BEE -> SimplePet(entity, graysPets)
                PetType.FOX -> FoxPet(entity, graysPets)
                PetType.OCELOT -> SimplePet(entity, graysPets)
            }
            graysPets.petMap[BasePet.getOwnerUUID(graysPets, entity)] = pet
            return pet
        }
    }

    enum class PetType { ARCTIC_FOX, BABY_ARCTIC_FOX, BABY_BEAR, BABY_BEE, BABY_BLACK_PANDA, BABY_BROWN_PANDA, BABY_CHICKEN, BABY_COW, BABY_DONKEY, BABY_FOX, BABY_HOPPY, BABY_HORSE, BABY_KITSUNE, BABY_MOOSHROOM, BABY_NICK, BABY_OCELOT, BABY_PIG, BABY_POG_CAT, BABY_POLEMISTIS, BABY_RABBIT, BABY_SHEEP, BABY_TURTLE, BEE, CHELSEA, CHICKEN, FOX, HOPPY, KITSUNE, NICK, OCELOT, PIG, POG_CAT, POLEMISTIS, RABBIT, TURTLE
    }
}
