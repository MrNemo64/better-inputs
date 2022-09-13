package me.nemo_64.betterinputs.bukkit;

import java.util.Objects;

import me.nemo_64.betterinputs.api.platform.IPlatformKey;

final class BukkitKey implements IPlatformKey {

    private final BukkitKeyProvider provider;
    private final String key;

    private String toString;

    public BukkitKey(final BukkitKeyProvider provider, final String key) {
        this.provider = Objects.requireNonNull(provider);
        this.key = Objects.requireNonNull(key, "String key can't be null");
    }
    
    final BukkitKeyProvider getProvider() {
        return provider;
    }
    
    @Override
    public String getNamespace() {
        return provider.getNamespace();
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        if (toString != null) {
            return toString;
        }
        return (toString = getNamespace() + ':' + key);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof IPlatformKey) {
            return equals((IPlatformKey) obj);
        }
        return obj != null && toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

}
