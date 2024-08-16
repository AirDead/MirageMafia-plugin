@file:Suppress("SYNTHETIC_PROPERTY_WITHOUT_JAVA_ORIGIN")

package com.mirage.mafiagame.models

data class Party(
    val id: String,
    val members: LinkedHashSet<Player> = linkedSetOf()
) {
    companion object {
        fun fromString(data: String): Party {
            val parts = data.split(';')
            val id = parts[0]
            val members = parts.getOrNull(1)
                ?.split(',')
                ?.map { Player.fromString(it) }
                ?.toCollection(LinkedHashSet())
                ?: linkedSetOf()
            return Party(id, members)
        }
    }

    fun toStringFormat(): String {
        val membersString = members.joinToString(",") { it.toStringFormat() }
        return if (members.isEmpty) id else "$id;$membersString"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Party) return false
        return id == other.id && members == other.members
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + members.hashCode()
        return result
    }
}