package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.role.currentRole
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

object PlayerAttackPlayerListener : Listener {
    @EventHandler
    fun onPlayerAttackPlayer(event: EntityDamageByEntityEvent) {
        event.isCancelled = true

        val attacker = event.damager as? Player ?: return
        val victim = event.entity as? Player ?: return

        if (attacker.inventory.itemInMainHand.type == Material.GOLDEN_SWORD && attacker.currentRole?.canKill == true) {
            val lastKill = attacker.currentGame?.lastKillTime?.get(attacker.uniqueId) ?: 0
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastKill >= 60 * 1000) {
                attacker.currentGame?.onMafiaKill(victim)
                attacker.currentGame?.lastKillTime?.put(attacker.uniqueId, currentTime)
                attacker.sendActionBar(Component.text("Вы убили игрока!", NamedTextColor.RED))
            } else {
                attacker.sendActionBar(Component.text("Вы устали, не стоит так часто напрягаться!", NamedTextColor.YELLOW))
            }
        }
    }
}