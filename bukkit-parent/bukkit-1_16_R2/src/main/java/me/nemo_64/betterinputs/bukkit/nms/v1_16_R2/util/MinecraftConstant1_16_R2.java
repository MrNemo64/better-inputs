package me.nemo_64.betterinputs.bukkit.nms.v1_16_R2.util;

import java.util.Optional;
import java.util.function.BiFunction;

import org.bukkit.Location;

import net.minecraft.server.v1_16_R2.*;

public final class MinecraftConstant1_16_R2 {

    private MinecraftConstant1_16_R2() {
        throw new UnsupportedOperationException();
    }

    public static final ContainerAccess BETTER_NULL = new ContainerAccess() {

        @Override
        public final <T> Optional<T> a(BiFunction<World, BlockPosition, T> var1) {
            return Optional.empty();
        }

        public final Location getLocation() {
            return new Location(null, 0, 0, 0);
        }

    };

}