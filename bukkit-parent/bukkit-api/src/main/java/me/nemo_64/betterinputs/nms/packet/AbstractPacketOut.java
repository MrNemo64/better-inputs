package me.nemo_64.betterinputs.nms.packet;

public abstract class AbstractPacketOut extends AbstractPacket {

    @Override
    public final boolean isIncoming() {
        return false;
    }

    @Override
    public final boolean isOutgoing() {
        return true;
    }

}
