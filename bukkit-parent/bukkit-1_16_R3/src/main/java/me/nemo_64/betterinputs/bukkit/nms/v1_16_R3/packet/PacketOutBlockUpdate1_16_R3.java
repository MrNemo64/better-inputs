package me.nemo_64.betterinputs.bukkit.nms.v1_16_R3.packet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_16_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers;

import me.lauriichan.laylib.reflection.Accessor;
import me.nemo_64.betterinputs.api.util.Option;
import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;
import me.nemo_64.betterinputs.bukkit.nms.packet.PacketOutBlockUpdate;
import net.minecraft.server.v1_16_R3.*;

public final class PacketOutBlockUpdate1_16_R3 extends PacketOutBlockUpdate {

    private static final Accessor accessor = Accessor.of(PacketPlayOutBlockChange.class).findField("position", "a");
    
    private final PacketPlayOutBlockChange packet;

    public PacketOutBlockUpdate1_16_R3(final PacketPlayOutBlockChange packet) {
        this.packet = packet;
    }

    public PacketOutBlockUpdate1_16_R3(final ArgumentMap map) {
        Location location = map.require("location", Location.class);
        Option<CraftBlockData> blockData = map.get("state", BlockData.class).filter(data -> data instanceof CraftBlockData)
            .map(data -> (CraftBlockData) data);
        Option<Material> material = map.get("state", Material.class);
        map.throwIfMissing();
        IBlockData state = Blocks.AIR.getBlockData();
        if (blockData.isPresent()) {
            state = blockData.get().getState();
        } else if (material.isPresent()) {
            Block block = CraftMagicNumbers.getBlock(material.get());
            if (block != null) {
                state = block.getBlockData();
            }
        }
        this.packet = new PacketPlayOutBlockChange(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()),
            state);
    }

    @Override
    public Object asMinecraft() {
        return packet;
    }

    @Override
    public Location getLocation() {
        BlockPosition pos = (BlockPosition) accessor.getValue(packet, "position");
        return new Location(null, pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public BlockData getBlockData() {
        return CraftBlockData.fromData(packet.block);
    }

}