package me.nemo_64.betterinputs.bukkit.nms.packet;

public abstract class AbstractPacket {

    public abstract Object asMinecraft();

    public abstract boolean isOutgoing();

    public abstract boolean isIncoming();

}
