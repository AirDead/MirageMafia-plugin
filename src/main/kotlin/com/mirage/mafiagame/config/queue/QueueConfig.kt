package com.mirage.mafiagame.config.queue

import kotlinx.serialization.Serializable

@Serializable
data class QueueConfig(
    val queues: List<QueueSetting>
)

@Serializable
data class QueueSetting(
    val type: String,
    val playerCount: Int
)
