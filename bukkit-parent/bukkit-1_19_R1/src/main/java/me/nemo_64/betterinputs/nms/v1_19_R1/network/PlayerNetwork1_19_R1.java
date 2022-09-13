package me.nemo_64.betterinputs.nms.v1_19_R1.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import me.nemo_64.betterinputs.nms.v1_19_R1.PlayerAdapter1_19_R1;
import net.minecraft.network.protocol.Packet;

public class PlayerNetwork1_19_R1 {

    private final PacketManager1_19_R1 packetManager;

    private final PacketInHandler packetIn = new PacketInHandler(this);
    private final PacketOutHandler packetOut = new PacketOutHandler(this);

    private final PlayerAdapter1_19_R1 player;

    private boolean active = false;

    public PlayerNetwork1_19_R1(final PacketManager1_19_R1 packetManager, final PlayerAdapter1_19_R1 player) {
        this.packetManager = packetManager;
        this.player = player;
        setActive(true);
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

    public boolean isActive() {
        return active;
    }

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

    public boolean call(Packet<?> nmsPacket) {
        return packetManager.call(player, nmsPacket);
    }

}
