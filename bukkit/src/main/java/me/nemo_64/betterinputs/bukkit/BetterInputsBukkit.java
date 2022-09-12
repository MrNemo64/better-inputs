package me.nemo_64.betterinputs.bukkit;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.plugin.Plugin;

import me.lauriichan.laylib.reflection.ClassUtil;
import me.nemo_64.betterinputs.api.BetterInputs;
import me.nemo_64.betterinputs.api.platform.IPlatformKeyProvider;

public final class BetterInputsBukkit extends BetterInputs<Plugin> {

    private static final Class<?> plugin_class_loader = ClassUtil.findClass("org.bukkit.plugin.java.PluginClassLoader");

    private final ConcurrentHashMap<ClassLoader, BukkitKeyProvider> keys = new ConcurrentHashMap<>();

    /*
     * 
     */

    void enable() {

    }

    void disable() {

    }

    /*
     * 
     */

    @Override
    protected Plugin asPlatformIdentifyable(Object object) {
        if (!(object instanceof Plugin)) {
            return null;
        }
        return (Plugin) object;
    }

    @Override
    public IPlatformKeyProvider getKeyProvider(Plugin plugin) {
        Objects.requireNonNull(plugin, "Plugin can't be null!");
        ClassLoader loader = plugin.getClass().getClassLoader();
        if (plugin_class_loader.equals(loader.getClass())) {
            throw new IllegalArgumentException("Plugin is not loaded directly by bukkit");
        }
        if (keys.containsKey(loader)) {
            return keys.get(loader);
        }
        return keys.computeIfAbsent(loader, (i) -> new BukkitKeyProvider(plugin));
    }

}
