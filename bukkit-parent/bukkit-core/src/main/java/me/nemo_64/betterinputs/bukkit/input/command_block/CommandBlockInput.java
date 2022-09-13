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
import me.nemo_64.betterinputs.bukkit.nms.packet.listener.PacketManager;
import me.nemo_64.betterinputs.bukkit.util.direction.Direction;
import me.nemo_64.betterinputs.bukkit.util.direction.Vertical;

public final class CommandBlockInput extends AbstractInput<String> {

    private static final BlockData COMMAND_BLOCK_DATA = Bukkit.createBlockData(Material.COMMAND_BLOCK);

    private final VersionHandler versionHandler;
    private final PacketManager packetManager;

    public CommandBlockInput(final VersionHandler versionHandler) {
        this.versionHandler = versionHandler;
        this.packetManager = versionHandler.getPacketManager();
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
        player.setData("command", location);
        player.setData("input", this);
        player.getNetwork().setActive(true);
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
        completeValue(command);
    }

}
