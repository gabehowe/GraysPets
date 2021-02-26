package io.github.gabehowe.grayspets

import net.minecraft.server.v1_16_R3.EntityLiving
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntityLiving
import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.entity.*
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*


class PetsCommand(private val graysPets: GraysPets) : TabExecutor {

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        if (args.size == 1) {
            val list = PetFactory.PetType.values().map { it.toString().toLowerCase() }.toMutableList()
            list.add("wand")
            list.add("hide")
            return list
        }
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cOnly players can use that command")
            return true
        }
        if (args.size != 1) {
            return false
        }
        if (args[0].toLowerCase() == "wand") {
            if (!sender.inventory.containsAtLeast(ItemStack(Material.STICK), 1)) {
                sender.sendMessage("§cYou need at least one stick to complete this action")
                return true
            }
            val wand = ItemStack(Material.STICK)
            val wandMeta = wand.itemMeta
            wandMeta.persistentDataContainer.set(graysPets.stickKey, PersistentDataType.INTEGER, 1)
            wandMeta.setDisplayName("§6Ultimate Stick")
            for (i in sender.inventory.contents) {
                i ?: continue
                if (i.type != Material.STICK) {
                    continue
                }
                i.amount -= 1
                break
            }
            wand.itemMeta = wandMeta
            sender.inventory.addItem(wand)
            return true
        }
        if (!PetFactory.PetType.values().map { it.toString() }.contains(args[0].toUpperCase()) && args[0].toLowerCase() != "hide") {
            sender.sendMessage("§cInvalid pet type!")
            return true
        }
        if (sender.persistentDataContainer.has(graysPets.activePetKey, PersistentDataType.STRING)) {
            if(args[0].toLowerCase() == "hide") {
                if (graysPets.petMap[sender.uniqueId] == null) {
                    sender.sendMessage("§cYou need an active pet to do that!")
                    return true
                }
                val pet = graysPets.petMap[sender.uniqueId]!!
                if (pet.isHidden){
                    (pet.entity as LivingEntity).setAI(true)
                    (pet.entity as LivingEntity).isInvisible = false
                pet.isHidden = false
                }
                else if (!pet.isHidden) {
                    (pet.entity as LivingEntity).setAI(false)
                    (pet.entity as LivingEntity).isInvisible = true
                    pet.entity.teleport(Location(sender.world,sender.location.x, 10000.0, sender.location.z))
                    pet.isHidden = true
                }
                return true
            }
            if (Bukkit.getEntity(
                    UUID.fromString(
                        sender.persistentDataContainer.get(graysPets.activePetKey, PersistentDataType.STRING)
                    )
                ) == null) {
                sender.persistentDataContainer.remove(graysPets.activePetKey)
            } else {
                Bukkit.getEntity(
                    UUID.fromString(
                        sender.persistentDataContainer.get(graysPets.activePetKey, PersistentDataType.STRING)
                    )
                )!!.remove()
            }
        }
        var petType = args[0]
        petType = petType.toUpperCase()
         try {
            PetFactory.makePet(PetFactory.PetType.valueOf(petType), graysPets, sender)
        } catch (e: IllegalArgumentException) {
            Bukkit.broadcastMessage("frick you son of a bork")
        }


        return true
    }
}