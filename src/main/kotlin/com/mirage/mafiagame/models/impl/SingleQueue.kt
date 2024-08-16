package com.mirage.mafiagame.models.impl

import com.mirage.mafiagame.models.Party
import com.mirage.mafiagame.models.Player
import com.mirage.mafiagame.models.Queue

open class SingleQueue(
    val maxPlayers: Int,
    val processLogic: (List<Player>) -> Unit
) : Queue {
    val players: MutableSet<Player> = linkedSetOf()

    override fun addPlayer(player: Player): Boolean {
        return players.size < maxPlayers && players.add(player)
    }

    override fun removePlayer(player: Player): Boolean {
        if (!players.contains(player)) {
            return false
        }
        return players.remove(player)
    }

    override fun addParty(party: Party): Boolean {
        if (party.members.any { it in players }) return false
        return if (remainingSlots() >= party.members.size) {
            players.addAll(party.members)
            true
        } else false
    }

    override fun removeParty(party: Party): Boolean {
        if (!party.members.any { it in players }) {
            return false
        }
        return players.removeAll(party.members)
    }

    override fun isPlayerInQueue(player: Player): Boolean {
        return players.contains(player)
    }

    override fun isFull(): Boolean {
        return players.size >= maxPlayers
    }

    override fun currentCount(): Int {
        return players.size
    }

    override fun remainingSlots(): Int {
        return maxPlayers - players.size
    }

    override fun processQueue() {
        processLogic(players.toList())
        players.clear()
    }
}