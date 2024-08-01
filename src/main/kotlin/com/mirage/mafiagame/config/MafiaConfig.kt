package com.mirage.mafiagame.config

import com.mirage.mafiagame.task.Task
import org.bukkit.Location

data class MafiaConfig(
    val chestLocations: MutableList<Location>,
    val gameLocation: Location,
    val lobbyLocation: Location,
    val startMeetingLocation: Location,
    val endMeetingLocation: Location,
    val tasksLocations: Map<Location, Task>
)