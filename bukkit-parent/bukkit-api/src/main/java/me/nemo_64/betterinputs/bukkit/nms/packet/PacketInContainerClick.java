package me.nemo_64.betterinputs.bukkit.nms.packet;

import org.bukkit.inventory.ItemStack;

public abstract class PacketInContainerClick extends AbstractPacketIn {
    
    public static enum ClickAction {
        
        PICKUP,
        QUICK_MOVE,
        SWAP,
        CLONE,
        THROW,
        QUICK_CRAFT,
        PICKUP_ALL;
        
    }
    
    public abstract int getContainerId();
    
    public abstract int getSlot();
    
    public abstract ClickAction getAction();
    
    public abstract ItemStack getItemStack();
    
}
