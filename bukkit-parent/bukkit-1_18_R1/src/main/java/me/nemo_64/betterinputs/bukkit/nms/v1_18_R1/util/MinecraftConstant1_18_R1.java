package me.nemo_64.betterinputs.bukkit.nms.v1_18_R1.util;

import java.util.Optional;
import java.util.function.BiFunction;

import org.bukkit.Location;

import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;

public final class MinecraftConstant1_18_R1 {

    private MinecraftConstant1_18_R1() {
        throw new UnsupportedOperationException();
    }

    public static final ContainerLevelAccess BETTER_NULL = new ContainerLevelAccess() {

        @Override
        public final <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> var1) {
            return Optional.empty();
        }

        public final Location getLocation() {
            return new Location(null, 0, 0, 0);
        }

    };

}