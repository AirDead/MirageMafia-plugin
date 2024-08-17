package com.mirage.mafiagame.nms.npc

import com.mirage.mafiagame.ext.sendPackets
import com.mojang.authlib.GameProfile
import net.minecraft.ChatFormatting
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Pose
import net.minecraft.world.scores.Team
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*

object Corpse {
    private val corpsesMap = HashMap<UUID, MutableList<ServerPlayer>>()

    fun spawnCorpse(player: Player, name: String?, uuid: UUID, x: Double, y: Double, z: Double) {
        val serverPlayer = (player as CraftPlayer).handle
        val npc = ServerPlayer(serverPlayer.server, serverPlayer.serverLevel(), GameProfile(uuid, name)).apply {
            pose = Pose.SLEEPING
            setPos(x, y + 0.1, z)
        }

        val team = serverPlayer.server.scoreboard.run {
            getPlayerTeam("invisibleNPCs") ?: addPlayerTeam("invisibleNPCs").apply {
                setColor(ChatFormatting.BLACK)
                setNameTagVisibility(Team.Visibility.NEVER)
                setCollisionRule(Team.CollisionRule.NEVER)
            }
        }

        serverPlayer.server.scoreboard.addPlayerToTeam(npc.scoreboardName, team)

        player.sendPackets(
            ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, npc),
            ClientboundAddPlayerPacket(npc),
            ClientboundSetEntityDataPacket(npc.id, npc.entityData.packDirty() ?: emptyList())
        )

        corpsesMap.computeIfAbsent(player.uniqueId) { mutableListOf() }.add(npc)
    }

    fun clearAllCorpses(player: Player) {
        val serverPlayer = (player as CraftPlayer).handle
        corpsesMap.remove(player.uniqueId)?.forEach { npc ->
            serverPlayer.server.scoreboard.getPlayerTeam("invisibleNPCs")?.let { team ->
                serverPlayer.server.scoreboard.removePlayerFromTeam(npc.scoreboardName, team)
            }
            player.sendPackets(ClientboundRemoveEntitiesPacket(npc.id))
        }
    }
}