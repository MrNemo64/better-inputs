package me.nemo_64.betterinputs.bukkit.nms.v1_16_R2.packet;

import me.nemo_64.betterinputs.bukkit.nms.packet.PacketInRenameItem;
import net.minecraft.server.v1_16_R2.PacketPlayInItemName;

public final class PacketInRenameItem1_16_R2 extends PacketInRenameItem {
    
    private final PacketPlayInItemName packet;
    
    public PacketInRenameItem1_16_R2(final PacketPlayInItemName packet) {
        this.packet = packet;
    }

    @Override
    public String getName() {
        return packet.b();
    }

    @Override
    public Object asMinecraft() {
        return packet;
    }

}