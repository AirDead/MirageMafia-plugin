package com.mirage.mafiagame.command

import com.mirage.mafiagame.command.mafia.MafiaJoinCommand
import com.mirage.mafiagame.command.mafia.MafiaLeaveCommand
import com.mirage.mafiagame.i18n.MafiaMsg
import dev.nikdekur.minelib.command.ServiceServerRootCommand
import dev.nikdekur.minelib.command.api.CommandContext
import dev.nikdekur.minelib.plugin.ServerPlugin

class MafiaCommand(override val app: ServerPlugin) : ServiceServerRootCommand() {
    override val argsRequirement = 1
    override val isConsoleFriendly = false
    override val name = "mafia"
    override val permission = "mafia.command.mafia"
    override val usageMSG = MafiaMsg.Command.Mafia.USAGE

    init {
        addSubCommand(MafiaJoinCommand(app))
        addSubCommand(MafiaLeaveCommand(app))
    }

    override fun CommandContext.onCommand() {
        throwUsage()
    }
}