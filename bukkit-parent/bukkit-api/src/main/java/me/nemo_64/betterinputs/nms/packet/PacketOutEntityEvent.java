package me.nemo_64.betterinputs.nms.packet;

public abstract class PacketOutEntityEvent extends AbstractPacketOut {
    
    public abstract int getEntityId();
    
    public abstract byte getEventId();

}
