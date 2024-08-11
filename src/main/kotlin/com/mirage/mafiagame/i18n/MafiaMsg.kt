@file:Suppress("NOTHING_TO_INLINE")

package com.mirage.mafiagame.i18n

import dev.nikdekur.minelib.i18n.MSGHolder

inline fun msg(id: String, defaultText: String) = object : MSGHolder {
    override val id: String = id
    override val defaultText: String = defaultText
}

object MafiaMsg {
    object Queue {
        val JOIN = msg("queue.join", "You joined the queue")
        val LEAVE = msg("queue.leave", "You left the queue")
    }

    object Command {
        val MAFIA_USAGE = msg("mafia.usage", "Usage: /mafia <join|leave>")
        val SUDO_USAGE = msg("sudo.usage", "Usage: /sudo <player> <command>")
    }


}