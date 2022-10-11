package me.nemo_64.betterinputs.bukkit.nms.packet;

import org.bukkit.Location;

import me.nemo_64.betterinputs.bukkit.nms.BlockEntityType;

public abstract class PacketOutBlockEntityData extends AbstractPacketOut {
    
    public abstract Location getLocation();
    
    public abstract BlockEntityType getType();

}
