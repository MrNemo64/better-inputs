package me.nemo_64.betterinputs.bukkit.nms.v1_16_R2.network;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_16_R2.Packet;

@Sharable
public final class PacketOutHandler1_16_R2 extends ChannelOutboundHandlerAdapter {

    private final PlayerNetwork1_16_R2 network;

    public PacketOutHandler1_16_R2(final PlayerNetwork1_16_R2 network) {
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