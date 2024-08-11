@file:Suppress("NOTHING_TO_INLINE")

package com.mirage.mafiagame.nms.block

import com.github.retrooper.packetevents.util.Vector3i
import net.minecraft.core.BlockPos
import org.bukkit.Location

inline fun Vector3i.toBlockPos() = BlockPos(this.x, this.y, this.z)
inline fun Location.toVector3i() = Vector3i(this.x.toInt(), this.y.toInt(), this.z.toInt())
inline fun Location.toBlockPos() = BlockPos(this.x.toInt(), this.y.toInt(), this.z.toInt())