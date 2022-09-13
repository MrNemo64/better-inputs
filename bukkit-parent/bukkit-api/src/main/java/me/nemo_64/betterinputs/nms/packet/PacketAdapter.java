package me.nemo_64.betterinputs.nms.packet;

public abstract class PacketAdapter {

    public abstract boolean isOutgoing();

    public final boolean isIncomming() {
        return !isOutgoing();
    }

}
