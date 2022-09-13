package me.nemo_64.betterinputs.nms.packet;

import org.bukkit.Location;

public abstract class PacketInUseItemOn extends AbstractPacketIn {

    public abstract Location getHitLocation();

    public abstract int getSequence();

}
