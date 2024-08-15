package com.mirage.mafiagame.command

import com.mirage.mafiagame.i18n.MafiaMsg
import com.mirage.mafiagame.queue.QueueService
import dev.nikdekur.minelib.command.ServiceServerCommand
import dev.nikdekur.minelib.command.api.CommandContext
import dev.nikdekur.minelib.command.api.CommandTabContext
import dev.nikdekur.minelib.ext.sendLangMsg
import dev.nikdekur.minelib.i18n.msg.MSGHolder
import dev.nikdekur.minelib.plugin.ServerPlugin
import org.koin.core.component.inject

class MafiaCommand(override val app: ServerPlugin) : ServiceServerCommand() {
    override val argsRequirement: Int = 0
    override val isConsoleFriendly: Boolean = false
    override val name: String = "mafia"
    override val permission: String = "mafia.command.mafia"
    override val usageMSG: MSGHolder = MafiaMsg.Command.MAFIA_USAGE

    val queueService by inject<QueueService>()

    override fun CommandContext.onCommand() {
        when (getString()) {
            "join" -> {
                val queueType = getStringOrNull() ?: run {
                    sender.sendLangMsg(MafiaMsg.Errors.NO_QUEUE_TYPE_PROVIDED)
                    return
                }

                if (!queueService.isValidQueueType(queueType)) {
                    sender.sendLangMsg(MafiaMsg.Errors.INVALID_QUEUE_TYPE, "queueType" to queueType)
                    return
                }

                queueService.joinQueue(player, queueType)
                sender.sendLangMsg(MafiaMsg.Queue.JOIN, "queueType" to queueType)

                val availableQueues = queueService.getAvailableQueues().joinToString(", ")
                sender.sendLangMsg(MafiaMsg.Queue.AVAILABLE_QUEUES, "queues" to availableQueues)
            }
            "leave" -> {
                val queueType = getStringOrNull() ?: run {
                    sender.sendLangMsg(MafiaMsg.Errors.NO_QUEUE_TYPE_PROVIDED)
                    return
                }

                queueService.leaveQueue(player, queueType)
                sender.sendLangMsg(MafiaMsg.Queue.LEAVE, "queue" to queueType)
            }
            else -> {
                sender.sendLangMsg(MafiaMsg.Command.MAFIA_USAGE)
            }
        }
    }

    override fun CommandTabContext.onTabComplete(): MutableList<String> {
        return when (args.size) {
            1 -> mutableListOf("join", "leave")
            2 -> {
                queueService.getAvailableQueues().toMutableList()
            }

            else -> mutableListOf()
        }
    }
}