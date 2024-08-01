@file:Suppress("UNCHECKED_CAST")

package com.mirage.mafiagame.module

import org.bukkit.plugin.java.JavaPlugin

abstract class BaseModule<T : BaseModule<T>>(override val app: JavaPlugin) : Module {
    init {
        DIContainer.register(this::class.java as Class<T>, this as T)
    }

    abstract override fun onLoad()
    abstract override fun onUnload()
}