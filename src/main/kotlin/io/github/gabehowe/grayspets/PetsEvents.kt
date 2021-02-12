package io.github.gabehowe.grayspets

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.util.ArrayList




class PetsEvents(private val graysPets: GraysPets) : Listener {
    @EventHandler
    fun onJoinEvent(event: PlayerJoinEvent) {
        graysPets.config.set("player", event.player.name)
        graysPets.saveConfig()
    }

}