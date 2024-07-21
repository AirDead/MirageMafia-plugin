package com.mirage.mafiagame.command

import com.mirage.mafiagame.game.impl.MafiaGame
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class TestCmd(
    val plugin: JavaPlugin
) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender is Player) {
            val testLocation = Location(Bukkit.getWorld("world"), 155.0, 89.0, 1134.0)
            val testInventory = Bukkit.createInventory(null, 9, "Test Inventory")

            MafiaGame(plugin, listOf(sender), mapOf(
                Pair(testLocation, testInventory)
            )).start()

        }

        return true
    }
}