package com.mirage.mafiagame.nms.block

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import java.util.concurrent.ConcurrentHashMap

object BlockTypeStorage {
    private val blockMap = ConcurrentHashMap<Location, Block>()

    fun setBlock(location: Location, block: Block): Block {
        blockMap[location] = block
        return block
    }

    fun getBlock(location: Location): Block? = blockMap[location]

    fun isBlockExist(location: Location): Boolean = blockMap.containsKey(location)

    fun updateBlockType(location: Location, newType: Material): Block? {
        return blockMap[location]?.apply { type = newType }
    }
}
