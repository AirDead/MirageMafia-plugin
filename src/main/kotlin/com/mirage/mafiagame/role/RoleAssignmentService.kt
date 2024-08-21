package com.mirage.mafiagame.role

import com.mirage.mafiagame.ext.asText
import com.mirage.mafiagame.role.impl.Captain
import com.mirage.mafiagame.role.impl.CoCock
import com.mirage.mafiagame.role.impl.Cock
import com.mirage.mafiagame.role.impl.Detective
import com.mirage.mafiagame.role.impl.FirstMate
import com.mirage.mafiagame.role.impl.JuniorMechanic
import com.mirage.mafiagame.role.impl.Mafia
import com.mirage.mafiagame.role.impl.Sailor
import com.mirage.mafiagame.role.impl.SeniorMechanic
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.ndkore.service.Service
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass

class RoleAssignmentService(override val app: ServerPlugin) : RoleService {
    override val bindClass: KClass<out Service<*>>
        get() = RoleService::class

    val availableRoles: MutableList<Role> = mutableListOf()

    override fun onLoad() {
        availableRoles.addAll(listOf(Captain(), FirstMate(), Detective(), SeniorMechanic(), JuniorMechanic(), Cock(), CoCock()))
    }

    override fun onUnload() {
        availableRoles.clear()
    }

    override fun assignRoles(players: List<Player>) {
        val mafiaCount = getMafiaCount(players.size)
        val shuffledPlayers = players.shuffled()
        val assignedRolesMap = mutableMapOf<Player, Role>()

        shuffledPlayers.forEachIndexed { index, player ->
            val role = if (index < availableRoles.size) availableRoles[index] else Sailor()
            assignedRolesMap[player] = role
        }

        val mafiaPlayers = shuffledPlayers.take(mafiaCount)

        assignedRolesMap.forEach { (player, role) ->
            val finalRole = if (mafiaPlayers.contains(player)) {
                object : Role by role {
                    override val name = role.name.color(NamedTextColor.GREEN)
                        .append(" (Мафия)".asText(NamedTextColor.RED))
                    override var canBreak = true
                    override var canKill = true

                    override fun getInventory(): List<ItemStack> {
                        return role.getInventory() + Mafia().getInventory()
                    }
                }
            } else {
                object : Role by role {
                    override val name = role.name
                }
            }

            player.currentRole = finalRole
            player.inventory.addItem(*finalRole.getInventory().toTypedArray())
            player.sendMessage("Ваша роль:")
            player.sendMessage(finalRole.name)
        }
    }

    override fun getMafiaCount(playerCount: Int) = when {
        playerCount >= 28 -> 5
        playerCount >= 24 -> 4
        playerCount >= 18 -> 3
        playerCount >= 14 -> 2
        playerCount >= 10 -> 1
        else -> 1
    }
}