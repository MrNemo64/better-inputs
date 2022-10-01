package me.nemo_64.betterinputs.bukkit.input.anvil;

import me.nemo_64.betterinputs.bukkit.nms.PlayerAdapter;
import me.nemo_64.betterinputs.bukkit.nms.packet.PacketInContainerClick;
import me.nemo_64.betterinputs.bukkit.nms.packet.PacketInContainerClose;
import me.nemo_64.betterinputs.bukkit.nms.packet.PacketInRenameItem;
import me.nemo_64.betterinputs.bukkit.nms.packet.listener.IPacketListener;
import me.nemo_64.betterinputs.bukkit.nms.packet.listener.PacketContainer;
import me.nemo_64.betterinputs.bukkit.nms.packet.listener.PacketHandler;
import me.nemo_64.betterinputs.bukkit.nms.packet.listener.PacketManager;

public final class AnvilPacketListener implements IPacketListener {

    private final PacketContainer container;

    public AnvilPacketListener(PacketManager manager) {
        this.container = manager.register(this);
    }

    public PacketContainer getContainer() {
        return container;
    }

    @PacketHandler
    public boolean receiveRenameItem(PlayerAdapter player, PacketInRenameItem packet) {
        if (packet.getName() == null || packet.getName().isBlank()) {
            return true;
        }
        player.setData("text", packet.getName());
        return true;
    }

    @PacketHandler
    public boolean receiveContainerClick(PlayerAdapter player, PacketInContainerClick packet) {
        if (packet.getSlot() != 2) {
            return true;
        }
        if (player.getData("input", AnvilInput.class).complete(player)) {
            player.closeMenu();
            return true;
        }
        return true;
    }

    @PacketHandler
    public boolean receiveContainerClose(PlayerAdapter player, PacketInContainerClose packet) {
        if (!player.getData("input", AnvilInput.class).complete(player)) {
            player.reopenMenu();
            return true;
        }
        return true;
    }

}
