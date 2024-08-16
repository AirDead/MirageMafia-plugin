package com.mirage.mafiagame.command

import com.mirage.mafiagame.i18n.MafiaMsg
import dev.nikdekur.minelib.command.ServiceServerCommand
import dev.nikdekur.minelib.command.api.CommandContext
import dev.nikdekur.minelib.command.api.CommandTabContext
import dev.nikdekur.minelib.i18n.msg.MSGHolder
import dev.nikdekur.minelib.plugin.ServerPlugin

class SudoCommand(override val app: ServerPlugin) : ServiceServerCommand() {
    override val argsRequirement: Int = 2
    override val isConsoleFriendly: Boolean = false
    override val name: String = "sudo"
    override val permission: String = "mafia.command.sudo"
    override val usageMSG: MSGHolder = MafiaMsg.Command.Sudo.USAGE

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
