package me.nemo_64.betterinputs.bukkit.nms.v1_20_R1.network;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.Packet;

@Sharable
public final class PacketOutHandler1_20_R1 extends ChannelOutboundHandlerAdapter {

    private final PlayerNetwork1_20_R1 network;

    public PacketOutHandler1_20_R1(final PlayerNetwork1_20_R1 network) {
        this.network = network;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof Packet)) {
            ctx.write(msg, promise);
            return;
        }
        Packet<?> packet = (Packet<?>) msg;
        if (network.call(packet)) {
            if (promise == null) {
                return;
            }
            promise.cancel(true);
            return;
        }
        ctx.write(packet, promise);
    }
}