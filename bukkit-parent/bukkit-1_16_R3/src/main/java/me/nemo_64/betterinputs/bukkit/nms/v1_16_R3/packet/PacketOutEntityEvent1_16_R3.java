package me.nemo_64.betterinputs.bukkit.nms.v1_16_R3.packet;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

import me.lauriichan.laylib.reflection.Accessor;
import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;
import me.nemo_64.betterinputs.bukkit.nms.packet.PacketOutEntityEvent;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityStatus;

public final class PacketOutEntityEvent1_16_R3 extends PacketOutEntityEvent {

    private static final Accessor accessor = Accessor.of(PacketPlayOutEntityStatus.class).findField("entityId", "a").findField("eventId", "b");

    private final PacketPlayOutEntityStatus packet;

    public PacketOutEntityEvent1_16_R3(final PacketPlayOutEntityStatus packet) {
        this.packet = packet;
    }

    public PacketOutEntityEvent1_16_R3(final ArgumentMap map) {
        Entity entity = map.require("entity", Entity.class);
        Number event = map.require("event", Number.class);
        map.throwIfMissing();
        if (!(entity instanceof CraftEntity)) {
            throw new IllegalArgumentException("Invalid entity");
        }
        this.packet = new PacketPlayOutEntityStatus(((CraftEntity) entity).getHandle(), event.byteValue());
    }

    @Override
    public Object asMinecraft() {
        return packet;
    }

    @Override
    public byte getEventId() {
        return (Byte) accessor.getValue(packet, "eventId");
    }

    @Override
    public int getEntityId() {
        return (Integer) accessor.getValue(packet, "entityId");
    }

}