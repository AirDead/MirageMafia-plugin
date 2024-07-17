package com.mirage.mafiagame.nms.npc

import com.mirage.packetapi.extensions.createNameStand
import com.mirage.packetapi.extensions.createNpc
import com.mirage.packetapi.extensions.createTeam
import com.mirage.packetapi.extensions.sendPackets
import net.minecraft.network.protocol.game.*
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.scores.PlayerTeam
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

class Corpse(
    uuid: UUID,
    name: String,
    private val isNameVisible: Boolean = false,
    location: Location
) {
    private val nameStand: ArmorStand = location.createNameStand(name)
    private val npc: ServerPlayer = location.createNpc(uuid, name, Pose.SLEEPING)
    private val team: PlayerTeam = npc.createTeam()

    init {
        Bukkit.getOnlinePlayers().forEach { player ->
            player.sendPackets(
                ClientboundSetPlayerTeamPacket.createRemovePacket(team),
                ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true)
            )
        }
    }

    fun show(vararg players: Player) {
        players.forEach { player ->
            player.sendPackets(
                ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, npc),
                ClientboundAddPlayerPacket(npc),
                ClientboundSetEntityDataPacket(npc.id, npc.entityData.packDirty())
            )
            if (isNameVisible) {
                player.sendPackets(
                    ClientboundAddEntityPacket(nameStand),
                    ClientboundSetEntityDataPacket(nameStand.id, nameStand.entityData.packDirty())
                )
            }
        }
    }

    fun hide(vararg players: Player) {
        players.forEach { player ->
            player.sendPackets(ClientboundRemoveEntitiesPacket(npc.id))
            if (isNameVisible) player.sendPackets(ClientboundRemoveEntitiesPacket(nameStand.id))
        }
    }
}