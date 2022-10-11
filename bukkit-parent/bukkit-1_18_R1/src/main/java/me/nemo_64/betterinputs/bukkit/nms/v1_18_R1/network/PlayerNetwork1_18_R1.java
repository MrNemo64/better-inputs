package me.nemo_64.betterinputs.bukkit.nms.v1_18_R1.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import me.nemo_64.betterinputs.bukkit.nms.IPlayerNetwork;
import me.nemo_64.betterinputs.bukkit.nms.v1_18_R1.PlayerAdapter1_18_R1;
import net.minecraft.network.protocol.Packet;

public class PlayerNetwork1_18_R1 implements IPlayerNetwork {

    private final PacketManager1_18_R1 packetManager;

    private final PacketInHandler1_18_R1 packetIn = new PacketInHandler1_18_R1(this);
    private final PacketOutHandler1_18_R1 packetOut = new PacketOutHandler1_18_R1(this);

    private final PlayerAdapter1_18_R1 player;

    private boolean active = true;

    public PlayerNetwork1_18_R1(final PacketManager1_18_R1 packetManager, final PlayerAdapter1_18_R1 player) {
        this.packetManager = packetManager;
        this.player = player;
        add(player.getChannel());
    }
    
    public PacketManager1_18_R1 packetManager() {
        return packetManager;
    }

    private void remove(Channel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        if (pipeline.get("bi:in") != null) {
            pipeline.remove("bi:in");
        }
        if (pipeline.get("bi:out") != null) {
            pipeline.remove("bi:out");
        }
    }

    private void add(Channel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        if (pipeline.get("bi:in") == null) {
            pipeline.addAfter("decoder", "bi:in", packetIn);
        }
        if (pipeline.get("bi:out") == null) {
            pipeline.addAfter("encoder", "bi:out", packetOut);
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        if (this.active == active) {
            return;
        }
        this.active = active;
        remove(player.getChannel());
        if (active) {
            add(player.getChannel());
            return;
        }
    }

    boolean call(Packet<?> nmsPacket) {
        return packetManager.call(player, nmsPacket);
    }

}