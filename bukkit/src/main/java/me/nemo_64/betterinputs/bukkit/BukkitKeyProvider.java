package me.nemo_64.betterinputs.bukkit;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.bukkit.plugin.Plugin;

import me.nemo_64.betterinputs.api.platform.IPlatformKey;
import me.nemo_64.betterinputs.api.platform.IPlatformKeyProvider;

class BukkitKeyProvider implements IPlatformKeyProvider {

    private static final Pattern VALID_KEY = Pattern.compile("[a-z0-9/._-]+");

    private final Plugin plugin;
    private final String namespace;

    private final ConcurrentHashMap<String, BukkitKey> keys = new ConcurrentHashMap<>();

    BukkitKeyProvider(Plugin plugin) {
        this.plugin = plugin;
        this.namespace = plugin.getName().toLowerCase(Locale.ROOT).replace(' ', '_');
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
        return keys.computeIfAbsent(key, this::createKey);
    }

    private BukkitKey createKey(String key) {
        if (key == null || !VALID_KEY.matcher(key).matches()) {
            throw new IllegalArgumentException("Invalid key. Must be [a-z0-9/._-]: " + key);
        }
        return new BukkitKey(this, key);
    }

}
