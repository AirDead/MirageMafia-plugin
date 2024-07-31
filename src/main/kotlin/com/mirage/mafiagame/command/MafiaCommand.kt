package com.mirage.mafiagame.command

import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.queue.QueueService
import com.mirage.mafiagame.queue.QueueType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class MafiaCommand(
    val queue: QueueService
) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be executed by a player.")
            return true
        }

        val onlinePlayers = Bukkit.getOnlinePlayers()

//        onlinePlayers.forEach {
//            sender.hidePlayer(plugin, it)
//            it.hidePlayer(plugin, sender)
//        }
        if (sender.currentGame == null) {
            queue.joinQueue(sender, QueueType.FIRST)
            sender.sendMessage(
                Component
                    .text("Вы зашли в очередь")
                    .color(NamedTextColor.GREEN)
            )
        } else if (args[0] == "start") {
            sender.currentGame?.startVoting(sender)
        } else if (args[0] == "stop") {
            sender.currentGame?.endVoting()
        } else {
            println("1")
        }



        return true
    }
}