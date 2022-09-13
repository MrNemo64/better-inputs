package me.nemo_64.betterinputs.bukkit;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.bukkit.plugin.Plugin;

import me.nemo_64.betterinputs.api.platform.IPlatformKey;
import me.nemo_64.betterinputs.api.platform.IPlatformKeyProvider;

final class BukkitKeyProvider implements IPlatformKeyProvider {

    private static final Pattern VALID_KEY = Pattern.compile("[a-z0-9/._-]+");

    private final Plugin plugin;
    private final String namespace;

    private final ConcurrentHashMap<String, BukkitKey> keys = new ConcurrentHashMap<>();

    private final BetterInputsBukkit bukkitApi;

    private boolean valid = true;

    BukkitKeyProvider(final BetterInputsBukkit bukkitApi, final Plugin plugin) {
        this.bukkitApi = bukkitApi;
        this.plugin = plugin;
        this.namespace = plugin.getName().toLowerCase(Locale.ROOT).replace(' ', '_');
    }

    public final ClassLoader getClassLoader() {
        return plugin.getClass().getClassLoader();
    }

    public final Plugin getPlugin() {
        return plugin;
    }

    @Override
    public final String getNamespace() {
        return namespace;
    }

    @Override
    public final IPlatformKey getKey(String key) {
        if (!valid) {
            throw new IllegalStateException("IPlatformKeyProvider is invalid; it was probably unregistered and cleared already");
        }
        return keys.computeIfAbsent(key.toLowerCase(Locale.ROOT), this::createKey);
    }

    private final BukkitKey createKey(String key) {
        if (key == null || !VALID_KEY.matcher(key).matches()) {
            throw new IllegalArgumentException("Invalid key. Must be [a-z0-9/._-]: " + key);
        }
        return new BukkitKey(this, key);
    }

    @Override
    public void unregisterAll() {
        BukkitKey[] keys = this.keys.values().toArray(BukkitKey[]::new);
        for (BukkitKey key : keys) {
            bukkitApi.unregisterInputFactory(key);
        }
        this.keys.clear();
        bukkitApi.removeProvider(this);
        valid = false;
    }

}
