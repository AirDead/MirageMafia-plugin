package com.mirage.mafiagame.command

import com.mirage.mafiagame.queue.QueueService
import com.mirage.mafiagame.queue.QueueType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TestCmd(
    val queue: QueueService
) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be executed by a player.")
            return true
        }

        queue.joinQueue(sender, QueueType.FIRST)
        sender.sendMessage(
            Component
                .text("Вы зашли в очередь")
                .color(NamedTextColor.GREEN)
        )

        return true
    }
}