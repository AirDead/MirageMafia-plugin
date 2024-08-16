package com.mirage.mafiagame.command

import com.mirage.mafiagame.command.mafia.MafiaJoinCommand
import com.mirage.mafiagame.command.mafia.MafiaLeaveCommand
import com.mirage.mafiagame.i18n.MafiaMsg
import com.mirage.mafiagame.queue.QueueService
import dev.nikdekur.minelib.command.ServiceServerRootCommand
import dev.nikdekur.minelib.command.api.CommandContext
import dev.nikdekur.minelib.command.api.CommandTabContext
import dev.nikdekur.minelib.i18n.msg.MSGHolder
import dev.nikdekur.minelib.plugin.ServerPlugin
import org.koin.core.component.inject
import java.util.LinkedList

class MafiaCommand(override val app: ServerPlugin) : ServiceServerRootCommand() {
    override val argsRequirement: Int = 1
    override val isConsoleFriendly: Boolean = false
    override val name: String = "mafia"
    override val permission: String = "mafia.command.mafia"
    override val usageMSG: MSGHolder = MafiaMsg.Command.Mafia.USAGE

    val queueService by inject<QueueService>()

    init {
        addSubCommand(MafiaJoinCommand(app))
        addSubCommand(MafiaLeaveCommand(app))
    }

    override fun CommandContext.onCommand() {
        throwUsage()
    }

    override fun CommandTabContext.onTabComplete(): MutableList<String>? {
        return subCommands.mapTo(LinkedList()) { it.name }
    }
}