package com.mirage.mafiagame.ext

import net.minecraft.network.protocol.Packet
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer
import org.bukkit.entity.Player

val Player.craftPlayer: CraftPlayer
    get() = this as CraftPlayer

fun Player.sendPackets(vararg packets: Packet<*>) {
    val connection = craftPlayer.handle.connection
    packets.forEach { connection.send(it) }
}

fun Player.sendPacket(packet: Packet<*>) = sendPackets(packet)

fun Player.sendPackets(packets: Collection<Packet<*>>) = sendPackets(*packets.toTypedArray())