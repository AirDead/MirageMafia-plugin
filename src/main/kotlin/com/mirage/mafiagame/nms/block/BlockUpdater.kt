package com.mirage.mafiagame.nms.block

import com.github.retrooper.packetevents.util.Vector3i
import net.minecraft.core.BlockPos
import org.bukkit.Location

fun Vector3i.toBlockPos() = BlockPos(this.x, this.y, this.z)
fun Location.toVector3i() = Vector3i(this.x.toInt(), this.y.toInt(), this.z.toInt())
fun Location.toBlockPos() = BlockPos(this.x.toInt(), this.y.toInt(), this.z.toInt())