package me.nemo_64.betterinputs.bukkit.nms.v1_19_R2.packet;

import java.lang.invoke.VarHandle;

import org.bukkit.craftbukkit.v1_19_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;

import me.lauriichan.laylib.reflection.ClassUtil;
import me.lauriichan.laylib.reflection.JavaAccess;
import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;
import me.nemo_64.betterinputs.bukkit.nms.packet.PacketOutEntityEvent;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;

public final class PacketOutEntityEvent1_19_R2 extends PacketOutEntityEvent {

    private static final VarHandle ENTITY_ID = JavaAccess
        .accessField(ClassUtil.getDeclaredField(ClientboundEntityEventPacket.class, int.class));

    private final ClientboundEntityEventPacket packet;

    public PacketOutEntityEvent1_19_R2(final ClientboundEntityEventPacket packet) {
        this.packet = packet;
    }

    public PacketOutEntityEvent1_19_R2(final ArgumentMap map) {
        Entity entity = map.require("entity", Entity.class);
        Number event = map.require("event", Number.class);
        map.throwIfMissing();
        if (!(entity instanceof CraftEntity)) {
            throw new IllegalArgumentException("Invalid entity");
        }
        this.packet = new ClientboundEntityEventPacket(((CraftEntity) entity).getHandle(), event.byteValue());
    }

    @Override
    public Object asMinecraft() {
        return packet;
    }

    @Override
    public byte getEventId() {
        return packet.getEventId();
    }

    @Override
    public int getEntityId() {
        Object object = ENTITY_ID.get(packet);
        if (object == null) {
            return -1;
        }
        return (Integer) object;
    }

}
