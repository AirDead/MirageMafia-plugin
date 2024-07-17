package com.mirage.mafiagame.command

import com.mirage.mafiagame.nms.npc.Corpse
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class TestCmd : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender is Player) {
            Corpse.spawnCorpse(sender, "Test", UUID.randomUUID(), sender.location.x, sender.location.y, sender.location.z)
        }

        return true
    }
}