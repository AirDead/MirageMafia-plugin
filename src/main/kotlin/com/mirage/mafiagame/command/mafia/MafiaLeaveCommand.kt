package com.mirage.mafiagame.command.mafia

import com.mirage.mafiagame.i18n.MafiaMsg
import com.mirage.mafiagame.queue.QueueService
import dev.nikdekur.minelib.command.ServiceServerCommand
import dev.nikdekur.minelib.command.api.CommandContext
import dev.nikdekur.minelib.command.api.CommandTabContext
import dev.nikdekur.minelib.ext.sendLangMsg
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.ndkore.service.inject

class MafiaLeaveCommand(override val app: ServerPlugin): ServiceServerCommand() {
    override val isConsoleFriendly = false
    override val name = "leave"
    override val permission = "mafia.command.leave"
    override val argsRequirement = 1
    override val usageMSG = MafiaMsg.Command.Mafia.LEAVE_USAGE

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

    override fun CommandTabContext.onTabComplete(): MutableList<String>? {
        return if (args.size == 1) {
            queueService.getAvailableQueues().toMutableList()
        } else {
            null
        }
    }
}
