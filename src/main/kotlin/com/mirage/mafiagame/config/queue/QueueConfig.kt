package com.mirage.mafiagame.config.queue

import kotlinx.serialization.Serializable

@Serializable
data class QueueConfig(
    val queues: List<QueueSettings> = listOf(QueueSettings("test", 1), QueueSettings("test2", 2))
)

@Serializable
data class QueueSettings(
    val name: String = "default",
    val playerCount: Int = 10
)
