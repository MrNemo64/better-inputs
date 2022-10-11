package me.nemo_64.betterinputs.bukkit.nms.v1_16_R1.packet;

import me.lauriichan.laylib.reflection.Accessor;
import me.nemo_64.betterinputs.bukkit.nms.packet.PacketInContainerClose;
import net.minecraft.server.v1_16_R1.PacketPlayInCloseWindow;

public final class PacketInContainerClose1_16_R1 extends PacketInContainerClose {

    private static final Accessor accessor = Accessor.of(PacketPlayInCloseWindow.class).findField("id", "id");

    private final PacketPlayInCloseWindow packet;

    public PacketInContainerClose1_16_R1(final PacketPlayInCloseWindow packet) {
        this.packet = packet;
    }

    @Override
    public int getContainerId() {
        return (Integer) accessor.getValue(packet, "id");
    }

    @Override
    public Object asMinecraft() {
        return packet;
    }

}