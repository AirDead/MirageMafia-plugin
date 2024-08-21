package com.mirage.mafiagame.command.mafia

import com.mirage.mafiagame.i18n.MafiaMsg
import com.mirage.mafiagame.queue.QueueService
import dev.nikdekur.minelib.command.ServiceServerCommand
import dev.nikdekur.minelib.command.api.CommandContext
import dev.nikdekur.minelib.command.api.CommandTabContext
import dev.nikdekur.minelib.ext.sendLangMsg
import dev.nikdekur.minelib.i18n.msg.MSGHolder
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.ndkore.service.inject
import kotlin.getValue

class MafiaLeaveCommand(override val app: ServerPlugin): ServiceServerCommand() {
    override val isConsoleFriendly: Boolean = false
    override val name: String = "leave"
    override val permission: String = "mafia.command.leave"
    override val argsRequirement: Int = 1
    override val usageMSG: MSGHolder = MafiaMsg.Command.Mafia.LEAVE_USAGE

    val queueService by inject<QueueService>()

    override fun CommandContext.onCommand() {
        val queueType = getString()

        if (!queueService.isValidQueueType(queueType)) {
            val availableQueues = queueService.getAvailableQueues().joinToString(", ")
            sender.sendLangMsg(MafiaMsg.Queue.AVAILABLE_QUEUES, "queues" to availableQueues)
            return
        }

        queueService.leaveQueue(player, queueType)
        sender.sendLangMsg(MafiaMsg.Queue.LEAVE, "queue" to queueType)
    }

    override fun CommandTabContext.onTabComplete(): MutableList<String> {
        return queueService.getAvailableQueues().toMutableList()
    }
}