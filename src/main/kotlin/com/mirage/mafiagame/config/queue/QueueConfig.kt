package com.mirage.mafiagame.config.queue

import kotlinx.serialization.Serializable

@Serializable
data class QueueConfig(
    val queues: List<QueueSettings> = emptyList()
)

@Serializable
data class QueueSettings(
    val name: String = "default",
    val playerCount: Int = 10
)
