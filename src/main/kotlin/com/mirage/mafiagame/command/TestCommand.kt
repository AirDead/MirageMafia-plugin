package com.mirage.mafiagame.command

import com.mirage.mafiagame.game.currentGame
import dev.nikdekur.minelib.command.ServiceServerCommand
import dev.nikdekur.minelib.command.api.CommandContext
import dev.nikdekur.minelib.command.api.CommandTabContext
import dev.nikdekur.minelib.i18n.msg.MessageReference
import dev.nikdekur.minelib.plugin.ServerPlugin

class TestCommand(override val app: ServerPlugin) : ServiceServerCommand() {
    override val name = "test"
    override val permission = "mafia.command.test"
    override val isConsoleFriendly = false
    override val argsRequirement: Int? = 1
    override val usageMSG: MessageReference? = null

    override fun CommandContext.onCommand() {
        val arg = getString()
        when (arg) {
            "1" -> {
                player.currentGame?.startNight()
            }
            "2" -> {
                player.currentGame?.endNight()
            }
            else -> {
                sender.sendMessage("You entered something else")
            }
        }
    }

    override fun CommandTabContext.onTabComplete(): MutableList<String>? {
        return mutableListOf("1", "2")
    }
}