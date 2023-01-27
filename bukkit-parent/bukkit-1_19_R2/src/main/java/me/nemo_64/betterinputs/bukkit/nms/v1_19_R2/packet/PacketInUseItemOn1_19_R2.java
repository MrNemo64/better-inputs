package me.nemo_64.betterinputs.bukkit.nms.v1_19_R2.packet;

import org.bukkit.Location;

import me.nemo_64.betterinputs.bukkit.nms.packet.PacketInUseItemOn;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;

public final class PacketInUseItemOn1_19_R2 extends PacketInUseItemOn {

    private final ServerboundUseItemOnPacket packet;

    public PacketInUseItemOn1_19_R2(final ServerboundUseItemOnPacket packet) {
        this.packet = packet;
    }

    @Override
    public Object asMinecraft() {
        return packet;
    }

    @Override
    public Location getHitLocation() {
        BlockPos pos = packet.getHitResult().getBlockPos();
        return new Location(null, pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public int getSequence() {
        return packet.getSequence();
    }

}
