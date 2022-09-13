package me.nemo_64.betterinputs.bukkit.input.command_block;

import me.nemo_64.betterinputs.nms.packet.PacketAdapter;
import me.nemo_64.betterinputs.nms.packet.listener.IPacketListener;
import me.nemo_64.betterinputs.nms.packet.listener.PacketHandler;

public final class CommandBlockPacketListener implements IPacketListener {

    @PacketHandler
    public boolean receiveSomePacket(PacketAdapter packet) {

        return true; // Returning true cancels the packet from being received from the server or sent to the client
    }

}
