package me.nemo_64.betterinputs.bukkit.nms.v1_20_R4.network;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.network.protocol.Packet;

@Sharable
public final class PacketInHandler1_20_R4 extends ChannelInboundHandlerAdapter {

    private final PlayerNetwork1_20_R4 network;

    public PacketInHandler1_20_R4(final PlayerNetwork1_20_R4 network) {
        this.network = network;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof Packet)) {
            ctx.fireChannelRead(msg);
            return;
        }
        Packet<?> packet = (Packet<?>) msg;
        if (network.call(packet)) {
            return;
        }
        ctx.fireChannelRead(packet);
    }

}