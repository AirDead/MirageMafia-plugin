package com.mirage.mafiagame.config

import org.bukkit.Location

interface ConfigService {

    var chestLocations: MutableList<Location>
    var gameLocation: Location
    var lobbyLocation: Location
    var meetingStartLocation: Location
    var meetingEndLocation: Location

    var bossBarNameDay: String
    var bossBarNameNight: String
    var nightDuration: Long
    var dayDuration: Long
    var sabotageDuration: Long
    var votingTitleMain: String
    var votingTitleSub: String
    var votingItemName: String
    var nightTitleMain: String
    var dayTitleMain: String
    var blockRepairActionBar: String
    var sabotageStartMessage: String
    var sabotageSuccessMessage: String
    var sabotageFailedMessage: String
    var votingSkippedMessage: String
    var votingNoResultMessage: String
    var votingExileMessage: String
    var blocksToBreak: Int
}