package com.mirage.mafiagame.models.impl

import com.mirage.mafiagame.models.Party
import com.mirage.mafiagame.models.Player
import com.mirage.mafiagame.models.Queue

class TeamQueue(
    val maxTeams: Int,
    val maxPlayersPerTeam: Int,
    val processLogic: (List<List<Player>>) -> Unit
) : Queue {
    val teams: MutableList<MutableList<Player>> = mutableListOf()

    override fun addPlayer(player: Player): Boolean {
        if (isPlayerInQueue(player)) return false

        for (team in teams) {
            if (team.size < maxPlayersPerTeam && team.none { it.name == player.name }) {
                team.add(player)
                return true
            }
        }
        if (teams.size < maxTeams) {
            teams.add(mutableListOf(player))
            return true
        }
        return false
    }

    override fun removePlayer(player: Player): Boolean {
        if (!isPlayerInQueue(player)) {
            return false
        }
        return teams.any { it.remove(player) }
    }

    override fun addParty(party: Party): Boolean {
        if (party.members.any { isPlayerInQueue(it) }) return false

        val remainingMembers = party.members.filter { member ->
            teams.flatten().none { it.name == member.name }
        }.toMutableList()

        for (team in teams) {
            while (team.size < maxPlayersPerTeam && remainingMembers.isNotEmpty()) {
                team.add(remainingMembers.removeAt(0))
            }
        }

        while (remainingMembers.isNotEmpty() && teams.size < maxTeams) {
            val newTeam = mutableListOf<Player>()
            while (newTeam.size < maxPlayersPerTeam && remainingMembers.isNotEmpty()) {
                newTeam.add(remainingMembers.removeAt(0))
            }
            teams.add(newTeam)
        }

        return remainingMembers.isEmpty()
    }

    override fun removeParty(party: Party): Boolean {
        if (!party.members.all { isPlayerInQueue(it) }) {
            return false
        }
        return teams.any { team -> team.removeAll(party.members) }
    }

    override fun isPlayerInQueue(player: Player): Boolean {
        return teams.flatten().contains(player)
    }

    override fun isFull(): Boolean {
        return teams.size >= maxTeams && teams.all { it.size == maxPlayersPerTeam }
    }

    override fun currentCount(): Int {
        return teams.sumOf { it.size }
    }

    override fun remainingSlots(): Int {
        return (maxTeams * maxPlayersPerTeam) - currentCount()
    }

    override fun processQueue() {
        processLogic(teams.toList())
        teams.clear()
    }
}
