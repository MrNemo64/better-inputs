package me.nemo_64.betterinputs.api.util.registry;

import java.util.Objects;

import me.nemo_64.betterinputs.api.platform.IPlatformKey;

public abstract class AbstractUnique implements IUnique {

    private final IPlatformKey key;

    /**
     * Creates a new unique object
     * 
     * @param key the key of this object
     * 
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public AbstractUnique(final IPlatformKey key) throws NullPointerException {
        this.key = Objects.requireNonNull(key, "Key can't be null");
    }

    /**
     * Gets the key of this unique object
     */
    @Override
    public final IPlatformKey getKey() {
        return key;
    }

}
