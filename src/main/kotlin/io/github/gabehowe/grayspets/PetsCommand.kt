package io.github.gabehowe.grayspets

import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Pig
import org.bukkit.entity.Player
import org.bukkit.entity.Rabbit
import org.bukkit.entity.Zombie
import org.bukkit.persistence.PersistentDataType

class PetsCommand(private val graysPets: GraysPets) : TabExecutor {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cOnly players can use that command")
            return true
        }
        var loc = sender.location
        loc.x = sender.location.x - 10
        val pet = sender.world.spawn(loc, Pig::class.java)
        pet.target = sender
        pet.persistentDataContainer.set(NamespacedKey(graysPets, "pet-pathfind"), PersistentDataType.INTEGER, 0)
        return true
    }
}