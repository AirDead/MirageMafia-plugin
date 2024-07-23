package com.mirage.mafiagame.game.listener

import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.role.currentRole
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

object PlayerAttackPlayerListener : Listener {
    @EventHandler
    fun onPlayerAttackPlayer(event: EntityDamageByEntityEvent) {
        val attacker = event.damager
        val victim = event.entity
        if (attacker !is Player || victim !is Player) return
        if (attacker.inventory.itemInMainHand.type != Material.IRON_SWORD) return

        if (attacker.currentRole?.canKill == true) {
            attacker.currentGame?.onMafiaKill(victim)
        }
    }
}