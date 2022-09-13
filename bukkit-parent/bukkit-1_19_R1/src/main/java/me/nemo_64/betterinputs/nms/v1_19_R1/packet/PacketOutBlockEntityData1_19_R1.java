package me.nemo_64.betterinputs.nms.v1_19_R1.packet;

import org.bukkit.Location;

import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;
import me.nemo_64.betterinputs.nms.BlockEntityType;
import me.nemo_64.betterinputs.nms.packet.PacketOutBlockEntityData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;

public final class PacketOutBlockEntityData1_19_R1 extends PacketOutBlockEntityData {

    private final ClientboundBlockEntityDataPacket packet;

    public PacketOutBlockEntityData1_19_R1(final ClientboundBlockEntityDataPacket packet) {
        this.packet = packet;
    }

    // TODO: Maybe allow NBT modification
    public PacketOutBlockEntityData1_19_R1(final ArgumentMap map) {
        Location location = map.require("location", Location.class);
        BlockEntityType entityType = map.require("type", BlockEntityType.class);
        map.throwIfMissing();
        BlockEntity entity = createBlockEntity(entityType, new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        if (entity == null) {
            throw new IllegalArgumentException("BlockEntityType '" + entityType.name() + "' is not supported!");
        }
        this.packet = ClientboundBlockEntityDataPacket.create(entity);
    }

    private BlockEntity createBlockEntity(BlockEntityType entityType, BlockPos pos) {
        switch (entityType) {
        case COMMAND_BLOCK:
            return new CommandBlockEntity(pos, Blocks.COMMAND_BLOCK.defaultBlockState());
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
        BlockPos pos = packet.getPos();
        return new Location(null, pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public BlockEntityType getType() {
        try {
            return BlockEntityType
                .valueOf(net.minecraft.world.level.block.entity.BlockEntityType.getKey(packet.getType()).getPath().toUpperCase());
        } catch (IllegalArgumentException exp) {
            return null;
        }
    }

}
