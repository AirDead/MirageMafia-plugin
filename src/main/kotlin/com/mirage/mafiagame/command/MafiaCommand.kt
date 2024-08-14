package com.mirage.mafiagame.command

import com.mirage.mafiagame.i18n.MafiaMsg
import com.mirage.mafiagame.queue.QueueService
import com.mirage.mafiagame.queue.QueueType
import dev.nikdekur.minelib.command.CommandContext
import dev.nikdekur.minelib.command.CommandTabContext
import dev.nikdekur.minelib.command.ServiceServerCommand
import dev.nikdekur.minelib.ext.sendLangMsg
import dev.nikdekur.minelib.i18n.msg.MSGHolder
import org.koin.core.component.inject

class MafiaCommand : ServiceServerCommand() {
    override val argsRequirement: Int = 0
    override val isConsoleFriendly: Boolean = false
    override val name: String = "mafia"
    override val permission: String = "mafia.command.mafia"
    override val usageMSG: MSGHolder = MafiaMsg.Command.MAFIA_USAGE

    val queueService: QueueService by inject()

    override fun CommandContext.onCommand() {
        when (getString()) {
            "join" -> {
                queueService.joinQueue(player, QueueType.FIRST)
                sender.sendLangMsg(MafiaMsg.Queue.JOIN)
            }
            "leave" -> {
                queueService.leaveQueue(player, QueueType.FIRST)
                sender.sendLangMsg(MafiaMsg.Queue.LEAVE)
            }
        }
    }


    override fun CommandTabContext.onTabComplete(): MutableList<String> {
        return mutableListOf("join", "leave")
    }


}