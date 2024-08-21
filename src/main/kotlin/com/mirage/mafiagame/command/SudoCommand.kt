package com.mirage.mafiagame.command

import com.mirage.mafiagame.i18n.MafiaMsg
import dev.nikdekur.minelib.command.ServiceServerCommand
import dev.nikdekur.minelib.command.api.CommandContext
import dev.nikdekur.minelib.command.api.CommandTabContext
import dev.nikdekur.minelib.plugin.ServerPlugin

class SudoCommand(override val app: ServerPlugin) : ServiceServerCommand() {
    override val argsRequirement = 2
    override val isConsoleFriendly = false
    override val name = "sudo"
    override val permission = "mafia.command.sudo"
    override val usageMSG = MafiaMsg.Command.Sudo.USAGE

    override fun CommandContext.onCommand() {
        val target = getOnlinePlayer()
        val command = getString()

        target.performCommand(command)
        sender.sendMessage("Command executed")
    }

    override fun CommandTabContext.onTabComplete(): MutableList<String>? {
        if (args.size == 1) {
            return online()
        }
        return null
    }


}
