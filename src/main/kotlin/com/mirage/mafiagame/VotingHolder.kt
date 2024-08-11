package com.mirage.mafiagame

import com.mirage.mafiagame.game.currentGame
import com.mirage.mafiagame.nms.item.NamedItemStack
import dev.nikdekur.minelib.gui.GUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class VotingMenu(player: Player, val players: List<Player>): GUI(player, 54) {
    override fun getTitle(): String = "Голосование"

    override fun beforeOpen() {
        super.beforeOpen()

        players.forEach {
            val item = NamedItemStack(Material.PLAYER_HEAD, it.name)
            inventory.addItem(item)
        }

        val barrier = NamedItemStack(Material.BARRIER, "Пропустить")

        inventory.addItem(barrier)
    }

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true

        val game = player.currentGame ?: return
        val clickedItem = event.currentItem ?: return

        when (clickedItem.type) {
            Material.BARRIER -> {
                if (game.skipVoters.add(player.uniqueId)) {
                    player.inventory.remove(Material.ENCHANTED_BOOK)
                    player.closeInventory()
                }
            }
            Material.PLAYER_HEAD -> {
                val target = clickedItem.itemMeta?.displayName ?: return
                if (game.kickVoters.add(player.uniqueId)) {
                    game.votingMap[target] = game.votingMap.getOrDefault(target, 0) + 1
                    player.inventory.remove(Material.ENCHANTED_BOOK)
                    player.closeInventory()
                }
            }

            else -> return
        }
    }
}