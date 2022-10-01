package me.nemo_64.betterinputs.bukkit.nms.v1_18_R2.packet;

import me.nemo_64.betterinputs.bukkit.nms.packet.PacketInRenameItem;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;

public final class PacketInRenameItem1_18_R2 extends PacketInRenameItem {
    
    private final ServerboundRenameItemPacket packet;
    
    public PacketInRenameItem1_18_R2(final ServerboundRenameItemPacket packet) {
        this.packet = packet;
    }

    @Override
    public String getName() {
        return packet.getName();
    }

    @Override
    public Object asMinecraft() {
        return packet;
    }

}