package me.nemo_64.betterinputs.bukkit.nms.v1_16_R1.packet;

import org.bukkit.Location;

import me.nemo_64.betterinputs.bukkit.nms.packet.PacketInUseItemOn;
import net.minecraft.server.v1_16_R1.BlockPosition;
import net.minecraft.server.v1_16_R1.PacketPlayInUseItem;

public final class PacketInUseItemOn1_16_R1 extends PacketInUseItemOn {

    private final PacketPlayInUseItem packet;

    public PacketInUseItemOn1_16_R1(final PacketPlayInUseItem packet) {
        this.packet = packet;
    }

    @Override
    public Object asMinecraft() {
        return packet;
    }

    @Override
    public Location getHitLocation() {
        BlockPosition pos = packet.c().getBlockPosition();
        return new Location(null, pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public int getSequence() {
        return 0;
    }

}