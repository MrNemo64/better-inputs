package me.nemo_64.betterinputs.bukkit.nms.v1_17_R1.network;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.Packet;

@Sharable
public final class PacketOutHandlerme extends ChannelOutboundHandlerAdapter {

    private final PlayerNetworkme network;

    public PacketOutHandlerme(final PlayerNetworkme network) {
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