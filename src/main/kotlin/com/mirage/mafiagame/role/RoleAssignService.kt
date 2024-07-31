package com.mirage.mafiagame.role

import com.mirage.mafiagame.module.BaseModule
import com.mirage.mafiagame.role.impl.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class RoleAssignService(app: JavaPlugin) : BaseModule<RoleAssignService>(app) {

    private lateinit var roles: MutableList<Role>

    override fun onLoad() {
        roles = mutableListOf(
            Captain(), FirstMate(), ChiefMechanic(), JuniorMechanic(), Cook(), AssistantCook(), PrivateDetective()
        )
    }

    override fun onUnload() {
        roles.clear()
    }

    fun assignRoles(players: List<Player>) {
        val mafiaCount = getMafiaCount(players.size)
        val shuffledPlayers = players.shuffled()
        val originalRoles = mutableMapOf<Player, Role>()

        shuffledPlayers.forEachIndexed { index, player ->
            val role = if (index < roles.size) roles[index] else Sailor()
            originalRoles[player] = role
        }

        val mafiaPlayers = shuffledPlayers.shuffled().take(mafiaCount)
        val assignedRoles = originalRoles.map { (player, role) ->
            if (mafiaPlayers.contains(player)) {
                player to object : Role by role {
                    override val name = "${role.name} (Мафия)"
                    override var canBreak = true
                    override var canKill = true

                    override fun getInventory(): List<ItemStack> {
                        return role.getInventory() + Mafia().getInventory()
                    }
                }
            } else {
                player to role
            }
        }

        assignedRoles.forEach { (player, role) ->
            player.currentRole = role
            player.inventory.addItem(*role.getInventory().toTypedArray())
            player.sendMessage("Ваша роль: ${role.name}")
        }
    }

    private fun getMafiaCount(playerCount: Int) = when {
        playerCount >= 28 -> 5
        playerCount >= 24 -> 4
        playerCount >= 18 -> 3
        playerCount >= 14 -> 2
        playerCount >= 10 -> 1
        else -> 1
    }
}