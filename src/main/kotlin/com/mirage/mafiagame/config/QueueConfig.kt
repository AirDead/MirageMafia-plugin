package com.mirage.mafiagame.config

import com.mirage.mafiagame.queue.QueueType
import kotlinx.serialization.Serializable

@Serializable
data class QueueConfig(
    val queues: List<QueueSetting>
)

@Serializable
data class QueueSetting(
    val type: QueueType,
    val playerCount: Int
)