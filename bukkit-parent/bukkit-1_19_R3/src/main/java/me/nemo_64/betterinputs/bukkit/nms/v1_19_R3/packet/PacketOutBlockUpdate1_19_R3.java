package me.nemo_64.betterinputs.bukkit.nms.v1_19_R3.packet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftMagicNumbers;

import me.nemo_64.betterinputs.api.util.Option;
import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;
import me.nemo_64.betterinputs.bukkit.nms.packet.PacketOutBlockUpdate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class PacketOutBlockUpdate1_19_R3 extends PacketOutBlockUpdate {

    private final ClientboundBlockUpdatePacket packet;

    public PacketOutBlockUpdate1_19_R3(final ClientboundBlockUpdatePacket packet) {
        this.packet = packet;
    }

    public PacketOutBlockUpdate1_19_R3(final ArgumentMap map) {
        Location location = map.require("location", Location.class);
        Option<CraftBlockData> blockData = map.get("state", BlockData.class).filter(data -> data instanceof CraftBlockData)
            .map(data -> (CraftBlockData) data);
        Option<Material> material = map.get("state", Material.class);
        map.throwIfMissing();
        BlockState state = Blocks.AIR.defaultBlockState();
        if (blockData.isPresent()) {
            state = blockData.get().getState();
        } else if (material.isPresent()) {
            Block block = CraftMagicNumbers.getBlock(material.get());
            if (block != null) {
                state = block.defaultBlockState();
            }
        }
        this.packet = new ClientboundBlockUpdatePacket(new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ()),
            state);
    }

    @Override
    public Object asMinecraft() {
        return packet;
    }

    @Override
    public Location getLocation() {
        BlockPos pos = packet.getPos();
        return new Location(null, pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public BlockData getBlockData() {
        return CraftBlockData.fromData(packet.getBlockState());
    }

}