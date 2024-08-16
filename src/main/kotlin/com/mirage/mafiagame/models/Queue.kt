package com.mirage.mafiagame.models

interface Queue {
    fun addPlayer(player: Player): Boolean
    fun removePlayer(player: Player): Boolean
    fun addParty(party: Party): Boolean
    fun removeParty(party: Party): Boolean
    fun isPlayerInQueue(player: Player): Boolean
    fun isFull(): Boolean
    fun currentCount(): Int
    fun remainingSlots(): Int
    fun processQueue()
}
