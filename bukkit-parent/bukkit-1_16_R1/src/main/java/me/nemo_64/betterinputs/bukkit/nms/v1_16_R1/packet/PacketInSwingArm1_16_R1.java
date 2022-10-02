package me.nemo_64.betterinputs.bukkit.nms.v1_16_R1.packet;

import me.nemo_64.betterinputs.bukkit.nms.packet.PacketInSwingArm;
import net.minecraft.server.v1_16_R1.EnumHand;
import net.minecraft.server.v1_16_R1.PacketPlayInArmAnimation;

public final class PacketInSwingArm1_16_R1 extends PacketInSwingArm {

    private final PacketPlayInArmAnimation packet;

    public PacketInSwingArm1_16_R1(final PacketPlayInArmAnimation packet) {
        this.packet = packet;
    }

    @Override
    public Object asMinecraft() {
        return packet;
    }
    
    @Override
    public Hand getHand() {
        return packet.b() == EnumHand.MAIN_HAND ? Hand.MAIN_HAND : Hand.OFF_HAND;
    }
    
}