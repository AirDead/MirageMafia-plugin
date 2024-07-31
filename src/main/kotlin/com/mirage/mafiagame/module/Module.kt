package com.mirage.mafiagame.module

import org.bukkit.plugin.java.JavaPlugin

interface Module {
    val app: JavaPlugin
    fun onLoad()
    fun onUnload()
}