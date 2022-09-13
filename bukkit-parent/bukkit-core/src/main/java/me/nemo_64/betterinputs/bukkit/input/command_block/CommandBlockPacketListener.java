package me.nemo_64.betterinputs.bukkit.input.command_block;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.nemo_64.betterinputs.nms.PlayerAdapter;
import me.nemo_64.betterinputs.nms.VersionHandler;
import me.nemo_64.betterinputs.nms.packet.PacketInSetCommandBlock;
import me.nemo_64.betterinputs.nms.packet.PacketInUseItemOn;
import me.nemo_64.betterinputs.nms.packet.listener.IPacketListener;
import me.nemo_64.betterinputs.nms.packet.listener.PacketContainer;
import me.nemo_64.betterinputs.nms.packet.listener.PacketHandler;
import me.nemo_64.betterinputs.nms.packet.listener.PacketManager;

public final class CommandBlockPacketListener implements IPacketListener {

    private final PacketContainer container;
    private final PacketManager manager;

    public CommandBlockPacketListener(PacketManager manager) {
        this.manager = manager;
        this.container = manager.register(this);
    }

    public PacketContainer getContainer() {
        return container;
    }

    @PacketHandler
    public boolean receiveUseItem(PlayerAdapter player, PacketInUseItemOn packet) {
        Location location = getLocation(player);
        if (location == null) {
            return false;
        }

        return true; // Returning true cancels the packet from being received from the server or sent to the client
    }

    @PacketHandler
    public boolean receiveSetCommandBlock(PlayerAdapter player, PacketInSetCommandBlock packet) {

        return true;
    }

    private Location getLocation(PlayerAdapter player) {
        Location location = player.getData("command", Location.class);
        Player bukkit = player.asBukkit();
    }

}
