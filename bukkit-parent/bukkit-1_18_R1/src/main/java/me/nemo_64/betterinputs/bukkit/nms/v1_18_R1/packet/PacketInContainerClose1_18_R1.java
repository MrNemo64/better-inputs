package me.nemo_64.betterinputs.bukkit.nms.v1_18_R1.packet;

import me.nemo_64.betterinputs.bukkit.nms.packet.PacketInContainerClose;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;

public final class PacketInContainerClose1_18_R1 extends PacketInContainerClose {

    private final ServerboundContainerClosePacket packet;

    public PacketInContainerClose1_18_R1(final ServerboundContainerClosePacket packet) {
        this.packet = packet;
    }

    @Override
    public int getContainerId() {
        return packet.getContainerId();
    }

    @Override
    public Object asMinecraft() {
        return packet;
    }

}