package com.mirage.mafiagame.network

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

class ModTransfer(vararg params: Any) {
    private val buffer = ByteArrayOutputStream()
    private val dataOutput = DataOutputStream(buffer)

    init {
        params.forEach { param ->
            when (param) {
                is Int -> dataOutput.writeInt(param)
                is Double -> dataOutput.writeDouble(param)
                is Boolean -> dataOutput.writeBoolean(param)
                is Float -> dataOutput.writeFloat(param)
                is Long -> dataOutput.writeLong(param)
                is String -> {
                    val strBytes = param.toByteArray(Charsets.UTF_8)
                    dataOutput.writeInt(strBytes.size)
                    dataOutput.write(strBytes)
                }
                else -> throw IllegalArgumentException("Unsupported type: ${param::class.simpleName}")
            }
        }
        dataOutput.flush()
        dataOutput.close()
    }

    fun send(plugin: JavaPlugin, channel: String, player: Player) {
        if (!plugin.server.messenger.isOutgoingChannelRegistered(plugin, channel)) {
            plugin.server.messenger.registerOutgoingPluginChannel(plugin, channel)
        }
        player.sendPluginMessage(plugin, channel, buffer.toByteArray())
    }
}
