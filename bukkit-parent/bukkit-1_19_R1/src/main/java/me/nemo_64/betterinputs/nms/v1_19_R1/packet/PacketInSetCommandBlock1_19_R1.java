package me.nemo_64.betterinputs.nms.v1_19_R1.packet;

import me.nemo_64.betterinputs.nms.packet.PacketInSetCommandBlock;
import net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket;

public final class PacketInSetCommandBlock1_19_R1 extends PacketInSetCommandBlock {

    private final ServerboundSetCommandBlockPacket packet;

    public PacketInSetCommandBlock1_19_R1(final ServerboundSetCommandBlockPacket packet) {
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
