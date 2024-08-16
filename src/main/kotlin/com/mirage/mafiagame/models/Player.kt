package com.mirage.mafiagame.models

data class Player(
    val name: String
) {
    companion object {
        fun fromString(data: String): Player {
            return Player(data)
        }
    }

    fun toStringFormat(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Player) return false
        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}