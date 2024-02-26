package me.nemo_64.betterinputs.bukkit.nms.v1_20_R2.packet;

import me.nemo_64.betterinputs.bukkit.nms.packet.PacketInSwingArm;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;

public final class PacketInSwingArm1_20_R2 extends PacketInSwingArm {

    private final ServerboundSwingPacket packet;

    public PacketInSwingArm1_20_R2(final ServerboundSwingPacket packet) {
        this.packet = packet;
    }

    @Override
    public Object asMinecraft() {
        return packet;
    }
    
    @Override
    public Hand getHand() {
        return packet.getHand() == InteractionHand.MAIN_HAND ? Hand.MAIN_HAND : Hand.OFF_HAND;
    }
    
}