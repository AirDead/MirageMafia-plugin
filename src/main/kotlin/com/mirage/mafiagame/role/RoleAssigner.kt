package com.mirage.mafiagame.role

import com.mirage.mafiagame.role.impl.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object RoleAssigner {
    private val roles = listOf(
        Captain(), FirstMate(), ChiefMechanic(), JuniorMechanic(), Cook(), AssistantCook(), PrivateDetective()
    )

    fun assignRoles(players: List<Player>) {
        val mafiaCount = getMafiaCount(players.size)
        val shuffledPlayers = players.shuffled()
        val assignedRoles = mutableListOf<Pair<Player, Role>>()
        val originalRoles = mutableMapOf<Player, Role>()

        shuffledPlayers.forEachIndexed { index, player ->
            val role = if (index < roles.size) roles[index] else Sailor()
            assignedRoles.add(player to role)
            originalRoles[player] = role
        }

        val mafiaPlayers = shuffledPlayers.shuffled().take(mafiaCount)
        mafiaPlayers.forEach { player ->
            val originalRole = originalRoles[player]!!
            assignedRoles.replaceAll { (p, role) ->
                if (p == player) {
                    player to object : Role by originalRole {
                        override val name = "${originalRole.name} (Мафия)"
                        override var canBreak = true
                        override var canKill = true

                        override fun getInventory(): List<ItemStack>  {
                            return originalRole.getInventory() + Mafia().getInventory()
                        }
                    }
                } else {
                    p to role
                }
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
