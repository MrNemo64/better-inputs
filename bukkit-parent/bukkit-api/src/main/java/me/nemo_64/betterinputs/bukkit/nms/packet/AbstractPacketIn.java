package me.nemo_64.betterinputs.bukkit.nms.packet;

public abstract class AbstractPacketIn extends AbstractPacket {

    @Override
    public final boolean isIncoming() {
        return true;
    }

    @Override
    public final boolean isOutgoing() {
        return false;
    }

}
