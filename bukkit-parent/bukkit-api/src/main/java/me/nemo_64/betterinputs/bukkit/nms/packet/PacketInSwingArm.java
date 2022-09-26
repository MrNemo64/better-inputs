package me.nemo_64.betterinputs.bukkit.nms.packet;

public abstract class PacketInSwingArm extends AbstractPacketIn {

    public static enum Hand {
        
        OFF_HAND,
        MAIN_HAND;
        
    }
    
    public abstract Hand getHand();

}
