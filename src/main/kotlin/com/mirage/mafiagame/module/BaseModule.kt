@file:Suppress("UNCHECKED_CAST")

package com.mirage.mafiagame.module

import org.bukkit.plugin.java.JavaPlugin


abstract class BaseModule(override val app: JavaPlugin) : Module {
    abstract override fun onLoad()
    abstract override fun onUnload()
}