package io.github.gabehowe.grayspets

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.*
import org.bukkit.persistence.PersistentDataType
import org.bukkit.*
import org.bukkit.attribute.Attribute

import org.bukkit.entity.Player
import java.util.*


class PetsCommand(private val graysPets: GraysPets) : TabExecutor {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        if (args.size == 1) {
            return graysPets.acceptablePetsList
        }
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cOnly players can use that command")
            return true
        }
        if (args.isEmpty()) {
            return false
        }
        if (sender.persistentDataContainer.has(NamespacedKey(graysPets, "active-pet"), PersistentDataType.STRING)) {
            if (Bukkit.getEntity(
                    UUID.fromString(
                        sender.persistentDataContainer.get(
                            NamespacedKey(
                                graysPets,
                                "active-pet"
                            ), PersistentDataType.STRING
                        )
                    )
                ) == null
            ) {
                sender.persistentDataContainer.remove(NamespacedKey(graysPets, "active-pet"))
            } else {
                Bukkit.getEntity(
                    UUID.fromString(
                        sender.persistentDataContainer.get(
                            NamespacedKey(
                                graysPets,
                                "active-pet"
                            ), PersistentDataType.STRING
                        )
                    )
                )!!.remove()
            }
        }
        if (!graysPets.acceptablePetsList.contains(args[0])) {
            sender.sendMessage("§cInvalid pet type!")
            return true
        }
        val loc = sender.location
        var isBaby = false
        var isHoppy = false
        var isPogCat = false
        var isNick = false
        var isBPanda = false
        var isChelsea = false
        var isKitsune = false
        var isGabeBear = false
        var petType = args[0]
        loc.x = sender.location.x + Math.random() * (2 - -2 + 1) + -2
        loc.z = sender.location.z + Math.random() * (2 - -2 + 1) + -2
        if (petType.contains("baby_", ignoreCase = true)) {
            petType = petType.removePrefix("baby_")
            isBaby = true
        }
        petType = petType.toLowerCase()
        if (petType == "bear") {
            petType = "POLAR_BEAR"
        }
        if (petType == "pog_cat") {
            petType = "OCELOT"
            isPogCat = true
        }
        if (petType == "katie_nick") {
            petType = "FOX"
            isNick = true
        }
        if (petType == "brown_panda") {
            petType = "PANDA"
            isBPanda = true
        }
        if (petType == "thomas_chelsea") {
            petType = "OCELOT"
            isChelsea = true
        }
        if (petType == "carissa_hoppy") {

            petType = "RABBIT"
            isHoppy = true
        }
        if (petType == "gray_kitsune") {
            petType = "FOX"
            isKitsune = true
        }
        if (petType == "gabriel_polemistis") {
            petType = "FOX"
            isGabeBear = true
        }
        petType = petType.toUpperCase()
        val pet = sender.world.spawnEntity(
            loc, EntityType.valueOf(
                petType
            )
        )
        pet as LivingEntity
        if (pet.type == EntityType.TURTLE && isBaby) {
            (pet as Turtle).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.baseValue = 0.5
        }
        if (isBaby) {
            (pet as Ageable).setBaby()
            (pet as Breedable).ageLock = true
        }
        if (pet.type == EntityType.FOX) {
            (pet as Fox).firstTrustedPlayer = sender
        }
        if (isHoppy) {
            (pet as Rabbit).rabbitType = Rabbit.Type.BLACK_AND_WHITE
            pet.customName = "§dHoppy" // flower trail
            pet.persistentDataContainer.set(NamespacedKey(graysPets, "flower-cooldown"), PersistentDataType.INTEGER, 0)
            pet.persistentDataContainer.set(NamespacedKey(graysPets, "pet-type"), PersistentDataType.STRING, "hoppy")
        }
        if (isPogCat) {
            (pet as Ocelot).customName = "pog cat"
            pet.persistentDataContainer.set(NamespacedKey(graysPets, "pet-type"), PersistentDataType.STRING, "bruh-cat")
        }
        if (isNick) {
            (pet as Fox).customName = "§cNick"
            pet.persistentDataContainer.set(NamespacedKey(graysPets, "pet-type"), PersistentDataType.STRING, "nick")
        }
        if (isBPanda) {
            (pet as Panda).mainGene = Panda.Gene.BROWN
            pet.hiddenGene = Panda.Gene.BROWN
        }
        if (isChelsea) {
            (pet as Ocelot).customName = "§bChelsea" // lightning cat
            pet.persistentDataContainer.set(NamespacedKey(graysPets, "pet-type"), PersistentDataType.STRING, "chelsea")
        }
        if (isKitsune) {
            (pet as Fox).customName = "§6Kitsune" // giant zombie
            pet.persistentDataContainer.set(NamespacedKey(graysPets, "pet-type"), PersistentDataType.STRING, "kitsune")
        }
        if (isGabeBear) {
            (pet as Fox).customName = "§2πολεμ§aιστής"
            pet.isGlowing = true
            pet.foxType = Fox.Type.SNOW
            pet.persistentDataContainer.set(
                NamespacedKey(graysPets, "pet-type"),
                PersistentDataType.STRING,
                "gabe-bear"
            )
        }
        sender.persistentDataContainer.set(
            NamespacedKey(graysPets, "active-pet"),
            PersistentDataType.STRING,
            pet.uniqueId.toString()
        )
        pet.isInvulnerable = true
        pet.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = 2048.0
        pet.health = 2048.0
        pet.persistentDataContainer.set(NamespacedKey(graysPets, "pet-pathfind"), PersistentDataType.INTEGER, 0)
        pet.persistentDataContainer.set(NamespacedKey(graysPets, "pet-cooldown"), PersistentDataType.INTEGER, 0)
        graysPets.petMap[sender.uniqueId] = pet
        pet.persistentDataContainer.set(NamespacedKey(graysPets, "is-pet"), PersistentDataType.INTEGER, 1)
        pet.persistentDataContainer.set(
            NamespacedKey(graysPets, "pet-owner"),
            PersistentDataType.STRING,
            sender.uniqueId.toString()
        )
        return true
    }
}