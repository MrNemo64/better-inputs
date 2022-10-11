package me.nemo_64.betterinputs.bukkit.nms.v1_16_R3.packet;

import org.bukkit.Location;

import me.lauriichan.laylib.reflection.Accessor;
import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;
import me.nemo_64.betterinputs.bukkit.nms.BlockEntityType;
import me.nemo_64.betterinputs.bukkit.nms.packet.PacketOutBlockEntityData;
import net.minecraft.server.v1_16_R3.*;

public final class PacketOutBlockEntityData1_16_R3 extends PacketOutBlockEntityData {
    
    private static final Accessor accessor = Accessor.of(PacketPlayOutTileEntityData.class).findField("id", "b").findField("position", "a");

    private final PacketPlayOutTileEntityData packet;

    public PacketOutBlockEntityData1_16_R3(final PacketPlayOutTileEntityData packet) {
        this.packet = packet;
    }

    // TODO: Maybe allow NBT modification
    public PacketOutBlockEntityData1_16_R3(final ArgumentMap map) {
        Location location = map.require("location", Location.class);
        BlockEntityType entityType = map.require("type", BlockEntityType.class);
        map.throwIfMissing();
        TileEntity entity = createBlockEntity(map, entityType,
            new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        if (entity == null) {
            throw new IllegalArgumentException("BlockEntityType '" + entityType.name() + "' is not supported!");
        }
        this.packet = new PacketPlayOutTileEntityData(entity.getPosition(), IRegistry.BLOCK_ENTITY_TYPE.a(entity.getTileType()), entity.save(new NBTTagCompound()));
    }

    private TileEntity createBlockEntity(ArgumentMap map, BlockEntityType entityType, BlockPosition pos) {
        switch (entityType) {
        case COMMAND_BLOCK:
            TileEntityCommand commandBlockEntity = new TileEntityCommand();
            commandBlockEntity.setPosition(pos);
            commandBlockEntity.getCommandBlock().setCommand(map.get("Command", String.class).orElse(""));
            return commandBlockEntity;
        default:
            return null;
        }
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
    public BlockEntityType getType() {
        try {
            return BlockEntityType.valueOf(TileEntityTypes
                .a(IRegistry.BLOCK_ENTITY_TYPE.fromId((Integer) accessor.getValue(packet, "id"))).getKey().toUpperCase());
        } catch (IllegalArgumentException exp) {
            return null;
        }
    }

}