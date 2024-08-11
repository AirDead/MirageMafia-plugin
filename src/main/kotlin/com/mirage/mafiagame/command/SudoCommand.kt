package com.mirage.mafiagame.command

import com.mirage.mafiagame.i18n.MafiaMsg
import dev.nikdekur.minelib.command.CommandContext
import dev.nikdekur.minelib.command.CommandTabContext
import dev.nikdekur.minelib.command.ServiceServerCommand
import dev.nikdekur.minelib.i18n.MSGHolder

class SudoCommand : ServiceServerCommand() {
    override val argsRequirement: Int = 2
    override val isConsoleFriendly: Boolean = false
    override val name: String = "sudo"
    override val permission: String = "mafia.command.sudo"
    override val usageMSG: MSGHolder = MafiaMsg.Command.SUDO_USAGE

    override fun CommandContext.onCommand() {
        val target = getOnlinePlayer(0)
        val command = getArg(1)

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
