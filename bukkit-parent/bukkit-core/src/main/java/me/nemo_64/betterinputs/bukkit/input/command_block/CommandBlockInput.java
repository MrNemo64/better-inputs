package me.nemo_64.betterinputs.bukkit.input.command_block;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import me.nemo_64.betterinputs.api.input.AbstractInput;
import me.nemo_64.betterinputs.api.input.InputProvider;
import me.nemo_64.betterinputs.api.platform.IPlatformActor;
import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;
import me.nemo_64.betterinputs.bukkit.nms.PlayerAdapter;
import me.nemo_64.betterinputs.bukkit.nms.VersionHandler;
import me.nemo_64.betterinputs.bukkit.nms.packet.PacketOutEntityEvent;
import me.nemo_64.betterinputs.bukkit.nms.packet.listener.PacketContainer;
import me.nemo_64.betterinputs.bukkit.nms.packet.listener.PacketManager;
import me.nemo_64.betterinputs.bukkit.util.direction.Direction;
import me.nemo_64.betterinputs.bukkit.util.direction.Vertical;

public final class CommandBlockInput extends AbstractInput<String> {

    private static final BlockData COMMAND_BLOCK_DATA = Bukkit.createBlockData(Material.COMMAND_BLOCK);

    private final VersionHandler versionHandler;
    private final PacketManager packetManager;

    private final PacketContainer listener;

    public CommandBlockInput(final VersionHandler versionHandler, final PacketContainer listener) {
        this.versionHandler = versionHandler;
        this.packetManager = versionHandler.getPacketManager();
        this.listener = listener;
    }

    @Override
    protected void onStart(InputProvider<String> provider, IPlatformActor<?> actor) {
        Player bukkitPlayer = actor.as(Player.class).getHandle();
        if (bukkitPlayer == null) {
            throw new IllegalStateException("Unsupported actor");
        }
        PlayerAdapter player = versionHandler.getPlayer(bukkitPlayer);
        player.send(packetManager.createPacket(new ArgumentMap().set("entity", bukkitPlayer).set("event", 26), PacketOutEntityEvent.class));
        Location location = bukkitPlayer.getEyeLocation();
        BlockFace face = getFace(location);
        location = location.add(face.getModX(), face.getModY(), face.getModZ());
        bukkitPlayer.sendBlockChange(location, COMMAND_BLOCK_DATA);
        if(location.getWorld() == null) {
            location.setWorld(player.asBukkit().getWorld());
        }
        player.setData("command", location);
        player.setData("input", this);
        listener.addUser(player.getUniqueId());
        // TODO: Possibly replace with modifier?
        bukkitPlayer.sendMessage("Enter input into command block");
    }

    private final BlockFace getFace(Location location) {
        Direction direction = new Direction(location.getYaw(), location.getPitch());
        Vertical vertical = direction.getVertical().normalize();
        if (vertical != Vertical.MID) {
            return BlockFace.valueOf(vertical.name());
        }
        return BlockFace.valueOf(direction.getHorizontal().normalize().name());
    }

    final void complete(String command) {
        removeActor();
        completeValue(command);
    }

    private final void removeActor() {
        PlayerAdapter adapter = versionHandler.getPlayer(provider().getActor().as(Player.class).getHandle());
        adapter.removeData("command");
        adapter.removeData("input");
        listener.removeUser(adapter.getUniqueId());
    }

    @Override
    protected boolean onCancel() {
        removeActor();
        return true;
    }

}
