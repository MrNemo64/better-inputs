package me.nemo_64.betterinputs.nms.packet;

public abstract class AbstractPacketAdapter {

    public abstract boolean isOutgoing();

    public final boolean isIncomming() {
        return !isOutgoing();
    }

}
