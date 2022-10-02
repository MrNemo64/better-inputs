package me.nemo_64.betterinputs.bukkit.nms.v1_16_R1.packet;

import me.nemo_64.betterinputs.bukkit.nms.packet.PacketInSetCommandBlock;
import net.minecraft.server.v1_16_R1.PacketPlayInSetCommandBlock;

public final class PacketInSetCommandBlock1_16_R1 extends PacketInSetCommandBlock {

    private final PacketPlayInSetCommandBlock packet;

    public PacketInSetCommandBlock1_16_R1(final PacketPlayInSetCommandBlock packet) {
        this.packet = packet;
    }

    @Override
    public Object asMinecraft() {
        return packet;
    }

    @Override
    public String getCommand() {
        return packet.c();
    }

}