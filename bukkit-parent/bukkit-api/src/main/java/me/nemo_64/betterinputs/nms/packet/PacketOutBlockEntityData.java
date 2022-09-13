package me.nemo_64.betterinputs.nms.packet;

import org.bukkit.Location;

import me.nemo_64.betterinputs.nms.BlockEntityType;

public abstract class PacketOutBlockEntityData extends AbstractPacketOut {
    
    public abstract Location getLocation();
    
    public abstract BlockEntityType getType();

}
