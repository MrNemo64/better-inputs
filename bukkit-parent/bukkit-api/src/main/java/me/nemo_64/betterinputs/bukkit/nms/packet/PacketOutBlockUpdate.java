package me.nemo_64.betterinputs.bukkit.nms.packet;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

public abstract class PacketOutBlockUpdate extends AbstractPacketOut {

    public abstract Location getLocation();

    public abstract BlockData getBlockData();

}
