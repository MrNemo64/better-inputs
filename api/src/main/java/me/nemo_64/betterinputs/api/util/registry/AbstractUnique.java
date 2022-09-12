package me.nemo_64.betterinputs.api.util.registry;

import java.util.Objects;

import me.nemo_64.betterinputs.api.platform.IPlatformKey;

public abstract class AbstractUnique implements IUnique {

    private final IPlatformKey key;

    public AbstractUnique(final IPlatformKey key) {
        this.key = Objects.requireNonNull(key);
    }

    @Override
    public final IPlatformKey getKey() {
        return key;
    }

}
