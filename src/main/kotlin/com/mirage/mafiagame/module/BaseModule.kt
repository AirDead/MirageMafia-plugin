package com.mirage.mafiagame.module

import dev.nikdekur.minelib.koin.MineLibKoinComponent
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.ndkore.module.Module

abstract class BaseModule(override val app: ServerPlugin) : Module<ServerPlugin>, MineLibKoinComponent