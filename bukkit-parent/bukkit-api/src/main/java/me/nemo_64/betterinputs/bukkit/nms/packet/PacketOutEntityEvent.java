package me.nemo_64.betterinputs.bukkit.nms.packet;

public abstract class PacketOutEntityEvent extends AbstractPacketOut {
    
    public abstract int getEntityId();
    
    public abstract byte getEventId();

}
