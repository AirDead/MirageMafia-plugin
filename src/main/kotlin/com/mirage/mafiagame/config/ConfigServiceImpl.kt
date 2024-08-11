package com.mirage.mafiagame.config

import com.mirage.mafiagame.module.BaseModule
import dev.nikdekur.minelib.plugin.ServerPlugin
import org.bukkit.Bukkit
import org.bukkit.Location

class ConfigServiceImpl(app: ServerPlugin) : BaseModule(app), ConfigService {

    override fun onLoad() {
        val gameWorld = Bukkit.getWorld("game")
        val config = app.config

        gameLocation = gameWorld?.spawnLocation ?: Location(gameWorld, 0.0, 65.0, 0.0)
        lobbyLocation = Location(Bukkit.getWorld("world"), 50.0, 65.0, 50.0)
        meetingStartLocation = Location(gameWorld, 203.0, 110.0, 4.0)
        meetingEndLocation = Location(gameWorld, 203.0, 110.0, 12.0)
        bossBarNameDay = config.getString("bossBarNameDay") ?: "День"
        bossBarNameNight = config.getString("bossBarNameNight") ?: "Ночь"
        nightDuration = config.getLong("nightDuration", 4 * 60 * 20L)
        dayDuration = config.getLong("dayDuration", 10 * 60 * 20L)
        sabotageDuration = config.getLong("sabotageDuration", 20 * 300L)
        votingTitleMain = config.getString("votingTitleMain") ?: "ГОЛОСОВАНИЕ"
        votingTitleSub = config.getString("votingTitleSub") ?: "Выберите кого вы хотите изгнать"
        votingItemName = config.getString("votingItemName") ?: "Голосование"
        nightTitleMain = config.getString("nightTitleMain") ?: "Ночь наступила!"
        dayTitleMain = config.getString("dayTitleMain") ?: "День наступил!"
        blockRepairActionBar = config.getString("blockRepairActionBar") ?: "Блок восстановлен, осталось: "
        sabotageStartMessage = config.getString("sabotageStartMessage") ?: "Саботаж начался"
        sabotageSuccessMessage = config.getString("sabotageSuccessMessage") ?: "САБОТАЖ УСПЕШНО УСТРАНЁН"
        sabotageFailedMessage = config.getString("sabotageFailedMessage") ?: "Саботаж не устранён, игра окончена"
        votingSkippedMessage = config.getString("votingSkippedMessage") ?: "Голосование пропущено"
        votingNoResultMessage = config.getString("votingNoResultMessage") ?: "Голосование не дало результата"
        votingExileMessage = config.getString("votingExileMessage") ?: "Игрок изгнан"
        blocksToBreak = config.getInt("blocksToBreak", 5)
    }

    override fun onUnload() {
        chestLocations.clear()
    }

    override var chestLocations: MutableList<Location> = mutableListOf()
    override lateinit var gameLocation: Location
    override lateinit var lobbyLocation: Location
    override lateinit var meetingStartLocation: Location
    override lateinit var meetingEndLocation: Location
    override lateinit var bossBarNameDay: String
    override lateinit var bossBarNameNight: String
    override var nightDuration: Long = 0L
    override var dayDuration: Long = 0L
    override var sabotageDuration: Long = 0L
    override lateinit var votingTitleMain: String
    override lateinit var votingTitleSub: String
    override lateinit var votingItemName: String
    override lateinit var nightTitleMain: String
    override lateinit var dayTitleMain: String
    override lateinit var blockRepairActionBar: String
    override lateinit var sabotageStartMessage: String
    override lateinit var sabotageSuccessMessage: String
    override lateinit var sabotageFailedMessage: String
    override lateinit var votingSkippedMessage: String
    override lateinit var votingNoResultMessage: String
    override lateinit var votingExileMessage: String
    override var blocksToBreak: Int = 0
}