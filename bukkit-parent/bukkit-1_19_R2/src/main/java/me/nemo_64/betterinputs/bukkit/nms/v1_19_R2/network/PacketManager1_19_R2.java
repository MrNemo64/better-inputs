package me.nemo_64.betterinputs.bukkit.nms.v1_19_R2.network;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import me.lauriichan.laylib.reflection.ClassUtil;
import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;
import me.nemo_64.betterinputs.api.util.argument.NotEnoughArgumentsException;
import me.nemo_64.betterinputs.bukkit.nms.packet.AbstractPacket;
import me.nemo_64.betterinputs.bukkit.nms.packet.AbstractPacketOut;
import me.nemo_64.betterinputs.bukkit.nms.packet.listener.PacketManager;
import me.nemo_64.betterinputs.bukkit.nms.v1_19_R2.PlayerAdapter1_19_R2;
import me.nemo_64.betterinputs.bukkit.nms.v1_19_R2.VersionHandler1_19_R2;
import net.minecraft.network.protocol.Packet;

public final class PacketManager1_19_R2 extends PacketManager {

    private static class NmsPacketBuilder<P extends Packet<?>> {

        private final Function<P, AbstractPacket> creator;
        private final Class<P> packetType;

        NmsPacketBuilder(final Class<P> packetType, final Function<P, AbstractPacket> creator) {
            this.packetType = packetType;
            this.creator = creator;
        }

        // We are sure that this is the correct packet type
        AbstractPacket build(Packet<?> packet) {
            return creator.apply(packetType.cast(packet));
        }

    }

    private Map<Class<?>, NmsPacketBuilder<?>> nmsBuilders = new HashMap<>();
    private Map<Class<?>, Function<ArgumentMap, ? extends AbstractPacketOut>> adapterBuilders = new HashMap<>();

    public PacketManager1_19_R2(VersionHandler1_19_R2 handler) {
        super(handler);
    }

    public final <P extends Packet<?>> void register(Class<P> packetType, Function<P, AbstractPacket> function) {
        if (!(nmsBuilders instanceof HashMap)) {
            return;
        }
        Objects.requireNonNull(packetType);
        Objects.requireNonNull(function);
        if (nmsBuilders.containsKey(packetType)) {
            return;
        }
        nmsBuilders.put(packetType, new NmsPacketBuilder<>(packetType, function));
    }

    public final <P extends AbstractPacketOut> void registerAdapter(Class<P> packetType, Function<ArgumentMap, ? extends P> function) {
        if (!(adapterBuilders instanceof HashMap)) {
            return;
        }
        Objects.requireNonNull(packetType);
        Objects.requireNonNull(function);
        if (adapterBuilders.containsKey(packetType)) {
            return;
        }
        adapterBuilders.put(packetType, function);
    }

    final boolean call(PlayerAdapter1_19_R2 player, Packet<?> nmsPacket) {
        NmsPacketBuilder<?> builder = nmsBuilders.get(nmsPacket.getClass());
        if (builder == null) {
            return false;
        }
        AbstractPacket packet = builder.build(nmsPacket);
        if (packet == null) {
            return false;
        }
        return call(player, packet);
    }

    @Override
    public <P extends AbstractPacketOut> P createPacket(ArgumentMap map, Class<P> packetType)
        throws NotEnoughArgumentsException, IllegalStateException, IllegalArgumentException {
        Function<ArgumentMap, ? extends AbstractPacket> function = adapterBuilders.get(packetType);
        if (function == null) {
            return null;
        }
        AbstractPacket packet = function.apply(map);
        if (packet == null || !packetType.isAssignableFrom(packet.getClass())) {
            throw new IllegalStateException("Invalid packet of type '" + ClassUtil.getClassName(packetType) + "'!");
        }
        return packetType.cast(packet);
    }

    public void close() {
        if (!(nmsBuilders instanceof HashMap)) {
            return;
        }
        this.nmsBuilders = Collections.unmodifiableMap(nmsBuilders);
        this.adapterBuilders = Collections.unmodifiableMap(adapterBuilders);
    }

}
