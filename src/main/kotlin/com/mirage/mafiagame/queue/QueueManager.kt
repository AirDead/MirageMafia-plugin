package com.mirage.mafiagame.queue

import com.mirage.mafiagame.models.Party
import com.mirage.mafiagame.models.Player
import com.mirage.mafiagame.models.impl.SingleQueue

class QueueManager(val createQueue: () -> SingleQueue) {
    val queues = mutableListOf(createQueue())

    fun addPlayerToQueue(player: Player): Boolean {
        if (isPlayerInAnyQueue(player)) {
            return false
        }
        val queue = queues.find { !it.isFull() } ?: createQueue().also { queues.add(it) }
        if (queue.addPlayer(player)) {
            if (queue.isFull()) processQueue(queue)
            return true
        }
        return false
    }

    fun removePlayerFromQueue(player: Player): Boolean {
        return queues.any { queue ->
            if (queue.isPlayerInQueue(player)) {
                queue.removePlayer(player)
            } else {
                false
            }
        }
    }

    fun addPartyToQueue(party: Party): Boolean {
        if (isPartyInAnyQueue(party)) {
            return false
        }
        val queue = queues.find { it.remainingSlots() >= party.members.size } ?: createQueue().also { queues.add(it) }
        if (queue.addParty(party)) {
            if (queue.isFull()) processQueue(queue)
            return true
        }
        return false
    }

    fun removePartyFromQueue(party: Party): Boolean {
        return queues.any { queue ->
            if (party.members.any { queue.isPlayerInQueue(it) }) {
                queue.removeParty(party)
            } else {
                false
            }
        }
    }

    fun isPlayerInAnyQueue(player: Player): Boolean {
        return queues.any { it.isPlayerInQueue(player) }
    }

    private fun isPartyInAnyQueue(party: Party): Boolean {
        return party.members.any { isPlayerInAnyQueue(it) }
    }

    private fun processQueue(queue: SingleQueue) {
        queue.processQueue()
        queues.remove(queue)
        queues.add(createQueue())
    }
}
