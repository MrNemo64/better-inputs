package me.nemo_64.betterinputs.nms.v1_19_R1.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.network.protocol.Packet;

final class PacketInHandler extends ChannelInboundHandlerAdapter {

    private final PlayerNetwork1_19_R1 network;

    public PacketInHandler(final PlayerNetwork1_19_R1 network) {
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
