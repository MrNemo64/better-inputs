package me.nemo_64.betterinputs.bukkit;

import java.util.Objects;

import me.nemo_64.betterinputs.api.platform.IPlatformKey;

final class BukkitKey implements IPlatformKey {

    private final BukkitKeyProvider provider;
    private final String key;

    public BukkitKey(final BukkitKeyProvider provider, final String key) {
        this.provider = Objects.requireNonNull(provider);
        this.key = Objects.requireNonNull(key, "String key can't be null");
    }

    @Override
    public String getNamespace() {
        return provider.getNamespace();
    }

    @Override
    public String getKey() {
        return key;
    }

}
