package me.nemo_64.betterinputs.bukkit.nms.v1_21_R1.packet;

import me.nemo_64.betterinputs.bukkit.nms.packet.PacketInSetCommandBlock;
import net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket;

public final class PacketInSetCommandBlock1_21_R1 extends PacketInSetCommandBlock {

    private final ServerboundSetCommandBlockPacket packet;

    public PacketInSetCommandBlock1_21_R1(final ServerboundSetCommandBlockPacket packet) {
        this.packet = packet;
    }

    @Override
    public Object asMinecraft() {
        return packet;
    }

    @Override
    public String getCommand() {
        return packet.getCommand();
    }

}