package me.nemo_64.betterinputs.bukkit;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.plugin.Plugin;

import me.lauriichan.laylib.reflection.ClassUtil;
import me.nemo_64.betterinputs.api.BetterInputs;
import me.nemo_64.betterinputs.api.input.AbstractInput;
import me.nemo_64.betterinputs.api.input.InputFactory;
import me.nemo_64.betterinputs.api.platform.IPlatformActor;
import me.nemo_64.betterinputs.api.platform.IPlatformKey;
import me.nemo_64.betterinputs.api.platform.IPlatformKeyProvider;

public final class BetterInputsBukkit extends BetterInputs<Plugin> {

    private static final Class<?> plugin_class_loader = ClassUtil.findClass("org.bukkit.plugin.java.PluginClassLoader");

    private final ConcurrentHashMap<ClassLoader, BukkitKeyProvider> keys = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<BukkitKey, InputFactory<?, ?>> factories = new ConcurrentHashMap<>();

    final void shutdown() {
        inputTick.shutdown();
    }

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
        return keys.computeIfAbsent(loader, (i) -> new BukkitKeyProvider(this, plugin));
    }

    @Override
    public <E> Optional<IPlatformActor<E>> getActor(E actor) {

        return null;
    }

    @SuppressWarnings({
        "unlikely-arg-type",
        "unchecked"
    })
    @Override
    public <T> Optional<InputFactory<T, ? extends AbstractInput<T>>> getInputFactory(String namespacedKey, Class<T> inputType) {
        Objects.requireNonNull(namespacedKey, "String namespacedKey can't be null");
        Objects.requireNonNull(inputType, "Class inputType can't be null");
        InputFactory<?, ?> factory = factories.get(namespacedKey.toLowerCase());
        if (factory == null || !inputType.isAssignableFrom(factory.getInputType())) {
            return Optional.empty();
        }
        return Optional.of((InputFactory<T, ? extends AbstractInput<T>>) factory);
    }

    @Override
    public <T> void registerInputFactory(InputFactory<T, ? extends AbstractInput<T>> factory) {
        Objects.requireNonNull(factory, "InputFactory can't be null");
        IPlatformKey platformKey = factory.getKey();
        if (!(platformKey instanceof BukkitKey)) {
            throw new IllegalArgumentException("IPlatformKey of InputFactory is not created by BetterInputs");
        }
        BukkitKey key = (BukkitKey) platformKey;
        if (factories.containsKey(key)) {
            throw new IllegalStateException("A InputFactory with key '" + key.toString() + "' already exists!");
        }
        factories.put(key, factory);
    }

    @Override
    public boolean unregisterInputFactory(IPlatformKey platformKey) {
        Objects.requireNonNull(platformKey, "IPlatformKey can't be null!");
        if (!(platformKey instanceof BukkitKey)) {
            throw new IllegalArgumentException("IPlatformKey is not created by BetterInputs");
        }
        return factories.remove((BukkitKey) platformKey) != null;
    }

    void removeProvider(BukkitKeyProvider provider) {
        Objects.requireNonNull(provider, "BukkitKeyProvider can't be null!");
        keys.remove(provider.getClassLoader());
    }

}
