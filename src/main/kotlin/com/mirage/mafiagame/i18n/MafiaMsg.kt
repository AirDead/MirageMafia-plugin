@file:Suppress("NOTHING_TO_INLINE")

package com.mirage.mafiagame.i18n

import dev.nikdekur.minelib.i18n.msg.MSGHolder

inline fun msg(id: String, defaultText: String) = object : MSGHolder {
    override val id: String = id
    override val bundle: String = "mafia"
    override val defaultText: String = defaultText
}

object MafiaMsg {

    object Queue {
        val JOIN = msg("queue.join", "You joined the queue {queueType}")
        val LEAVE = msg("queue.leave", "You left the queue {queueType}")
        val AVAILABLE_QUEUES = msg("queue.available_queues", "Available queues: {queues}")
    }

    object Command {
        val MAFIA_USAGE = msg("mafia.usage", "Usage: /mafia <join|leave> [queueType]")
        val SUDO_USAGE = msg("sudo.usage", "Usage: /sudo <player> <command>")
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
