package me.nemo_64.betterinputs.nms.v1_19_R1;

import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import io.netty.channel.Channel;
import me.nemo_64.betterinputs.nms.PlayerAdapter;
import me.nemo_64.betterinputs.nms.packet.AbstractPacketOut;
import me.nemo_64.betterinputs.nms.v1_19_R1.network.PacketManager1_19_R1;
import me.nemo_64.betterinputs.nms.v1_19_R1.network.PlayerNetwork1_19_R1;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;

public final class PlayerAdapter1_19_R1 extends PlayerAdapter {

    private final PlayerNetwork1_19_R1 network;

    private final CraftPlayer bukkit;
    private final ServerPlayer minecraft;

    public PlayerAdapter1_19_R1(PacketManager1_19_R1 packetManager, Player player) {
        super(player.getUniqueId());
        this.bukkit = (CraftPlayer) player;
        this.minecraft = bukkit.getHandle();
        this.network = new PlayerNetwork1_19_R1(packetManager, this);
    }

    final void terminate() {

    }

    public PlayerNetwork1_19_R1 getNetwork() {
        return network;
    }

    public ServerPlayer asMinecraft() {
        return minecraft;
    }

    @Override
    public CraftPlayer asBukkit() {
        return bukkit;
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }

    @Override
    public void send(AbstractPacketOut... packets) {
        for (AbstractPacketOut packet : packets) {
            if (!(packet.asMinecraft() instanceof Packet)) {
                continue;
            }
            minecraft.connection.send((Packet<?>) packet.asMinecraft());
        }
    }

    @Override
    public void acknowledgeBlockChangesUpTo(int sequence) {
        minecraft.connection.ackBlockChangesUpTo(sequence);
    }

    public Channel getChannel() {
        return minecraft.connection.getConnection().channel;
    }

}
