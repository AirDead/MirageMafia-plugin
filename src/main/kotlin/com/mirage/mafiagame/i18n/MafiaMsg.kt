@file:Suppress("NOTHING_TO_INLINE")

package com.mirage.mafiagame.i18n

import dev.nikdekur.minelib.i18n.msg.I18nMessage
import dev.nikdekur.minelib.i18n.msg.MessageReference

inline fun msg(id: String, defaultText: String) = MafiaMessage(id, defaultText)

class MafiaMessage(override val id: String, override val defaultText: String) : I18nMessage, MessageReference {
    override val bundleId: String = "mafia"
    override val msg: I18nMessage = this
}

object MafiaMsg {

    object Queue {
        val JOIN = msg("queue.join", "You joined the queue {queue}")
        val LEAVE = msg("queue.leave", "You left the queue {queue}")
        val AVAILABLE_QUEUES = msg("queue.available_queues", "Available queues: {queues}")
    }

    object Command {
        object Mafia {
            val JOIN_USAGE = msg("mafia.join.usage", "Usage: /mafia join [queueType]")
            val LEAVE_USAGE = msg("mafia.leave.usage", "Usage: /mafia leave [queueType]")
            val USAGE = msg("mafia.usage", "Usage: /mafia <join|leave>")
        }
        object Sudo {
            val USAGE = msg("sudo.usage", "Usage: /sudo <player> <command>")
        }
    }

    object Errors {
        val NO_QUEUE_TYPE_PROVIDED = msg("error.no_queue_type_provided", "No queue type provided!")
        val INVALID_QUEUE_TYPE = msg("error.invalid_queue_type", "Invalid queue type provided: {queueType}")
        val PLAYER_NOT_FOUND = msg("error.player_not_found", "Player not found: {player}")
    }

    object Warnings {
        val QUEUE_ALREADY_FULL = msg("warning.queue_already_full", "The queue is already full: {queueType}")
        val PLAYER_ALREADY_IN_QUEUE = msg("warning.player_already_in_queue", "You are already in the queue: {queueType}")
    }
}
