package me.nemo_64.betterinputs.bukkit.input.command_block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.nemo_64.betterinputs.api.input.modifier.AttemptModifier;
import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;
import me.nemo_64.betterinputs.bukkit.nms.BlockEntityType;
import me.nemo_64.betterinputs.bukkit.nms.PlayerAdapter;
import me.nemo_64.betterinputs.bukkit.nms.packet.*;
import me.nemo_64.betterinputs.bukkit.nms.packet.listener.IPacketListener;
import me.nemo_64.betterinputs.bukkit.nms.packet.listener.PacketContainer;
import me.nemo_64.betterinputs.bukkit.nms.packet.listener.PacketHandler;
import me.nemo_64.betterinputs.bukkit.nms.packet.listener.PacketManager;

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
        Location target = packet.getHitLocation();
        if (target.getBlockX() != location.getBlockX() || target.getBlockY() != location.getBlockY()
            || target.getBlockZ() != location.getBlockZ()) {
            return false;
        }
        player.acknowledgeBlockChangesUpTo(packet.getSequence());
        player.send(new AbstractPacketOut[] {
            manager.createPacket(new ArgumentMap().set("location", location).set("type", BlockEntityType.COMMAND_BLOCK),
                PacketOutBlockEntityData.class),
            manager.createPacket(new ArgumentMap().set("location", location).set("material", Material.COMMAND_BLOCK),
                PacketOutBlockUpdate.class)
        });
        return true; // Returning true cancels the packet from being received from the server or sent to the client
    }

    @SuppressWarnings("unchecked")
    @PacketHandler
    public boolean receiveSetCommandBlock(PlayerAdapter player, PacketInSetCommandBlock packet) {
        CommandBlockInput input = player.getData("input", CommandBlockInput.class);
        AttemptModifier<String> modifier = (AttemptModifier<String>) input.provider().getModifier(AttemptModifier.class).orElse(null);
        if (modifier != null) {
            if (!modifier.attempt(packet.getCommand())) {
                // TODO: Move to modifier maybe?
                player.asBukkit().sendMessage("Please try again!");
                return true;
            }
        }
        player.removeData("input");
        Location location = player.getData("command", Location.class);
        player.removeData("command");
        container.asyncService().submit(() -> {
            player.getNetwork().setActive(false);
            input.complete(packet.getCommand());
            Player bukkit = player.asBukkit();
            player.send(manager.createPacket(new ArgumentMap().set("entity", bukkit).set("event", 24 + player.getPermissionLevel()),
                PacketOutEntityEvent.class));
            bukkit.sendBlockChange(location, bukkit.getWorld().getBlockAt(location).getBlockData());
            bukkit.closeInventory();
        });
        return true;
    }

    private Location getLocation(PlayerAdapter player) {
        Location location = player.getData("command", Location.class);
        Player bukkit = player.asBukkit();
        if (!bukkit.getWorld().equals(location.getWorld()) || bukkit.getEyeLocation().distance(location) > 6) {
            player.getNetwork().setActive(false);
            container.mainService().submit(() -> {
                player.send(manager.createPacket(new ArgumentMap().set("entity", bukkit).set("event", 24 + player.getPermissionLevel()),
                    PacketOutEntityEvent.class));
                bukkit.sendBlockChange(location, bukkit.getWorld().getBlockAt(location).getBlockData());
                bukkit.closeInventory();
            });
            return null;
        }
        return location;
    }

}
