package com.mirage.mafiagame.command

import com.mirage.mafiagame.queue.QueueService
import com.mirage.mafiagame.queue.QueueType
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TestCmd(
    val queue: QueueService
) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender is Player) {
            val testLocation = Location(Bukkit.getWorld("world"), 155.0, 89.0, 1134.0)
            val testInventory = Bukkit.createInventory(null, 9, "Test Inventory")

            queue.joinQueue(sender, QueueType.FIRST)

        }

        return true
    }
}