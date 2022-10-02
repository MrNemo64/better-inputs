package me.nemo_64.betterinputs.bukkit;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
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
    private final ConcurrentHashMap<String, InputFactory<?, ?>> factories = new ConcurrentHashMap<>();
    private final BetterInputsPlugin plugin;
    
    private List<String> inputKeys;
    
    public BetterInputsBukkit(final BetterInputsPlugin plugin) {
        this.plugin = plugin;
    }
    

    final void shutdown() {
        inputTick.shutdown();
    }

    public final List<String> getKeys() {
        if (inputKeys != null) {
            return inputKeys;
        }
        return inputKeys = factories.keySet().stream().collect(Collectors.toUnmodifiableList());
    }

    @Override
    protected Plugin asPlatformIdentifiable(Object object) {
        if (!(object instanceof Plugin)) {
            return null;
        }
        return (Plugin) object;
    }

    @Override
    public IPlatformKeyProvider getKeyProvider(Plugin plugin) {
        Objects.requireNonNull(plugin, "Plugin can't be null!");
        ClassLoader loader = plugin.getClass().getClassLoader();
        if (!plugin_class_loader.equals(loader.getClass())) {
            throw new IllegalArgumentException("Plugin is not loaded directly by bukkit");
        }
        if (keys.containsKey(loader)) {
            return keys.get(loader);
        }
        return keys.computeIfAbsent(loader, (i) -> new BukkitKeyProvider(this, plugin));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> Optional<IPlatformActor<E>> getActor(E actor) {
        if (actor == null || !(actor instanceof CommandSender)) {
            return Optional.empty();
        }
        return Optional.of((IPlatformActor<E>) new BukkitActor<>(CommandSender.class.cast(actor)));
    }

    @Override
    public Optional<InputFactory<?, ?>> getInputFactory(String namespacedKey) {
        Objects.requireNonNull(namespacedKey, "String namespacedKey can't be null");
        InputFactory<?, ?> factory = factories.get(namespacedKey.toLowerCase());
        if (factory == null) {
            return Optional.empty();
        }
        return Optional.of(factory);
    }

    @SuppressWarnings("unchecked")
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
        String key = ((BukkitKey) platformKey).toString();
        if (factories.containsKey(key)) {
            throw new IllegalStateException("A InputFactory with key '" + key + "' already exists!");
        }
        factories.put(key, factory);
        inputKeys = null;
    }

    @Override
    public boolean unregisterInputFactory(IPlatformKey platformKey) {
        Objects.requireNonNull(platformKey, "IPlatformKey can't be null!");
        if (!(platformKey instanceof BukkitKey)) {
            throw new IllegalArgumentException("IPlatformKey is not created by BetterInputs");
        }
        InputFactory<?, ?> factory = factories.remove(((BukkitKey) platformKey).toString());
        if (factory == null) {
            return false;
        }
        inputKeys = null;
        factory.onUnregister();
        return true;
    }
    
    @Override
    protected void updateInputStats(String providerType, Class<?> inputType) {
        plugin.updateStat(providerType, inputType);
    }

    void removeProvider(BukkitKeyProvider provider) {
        Objects.requireNonNull(provider, "BukkitKeyProvider can't be null!");
        keys.remove(provider.getClassLoader());
    }

    void unregister(Plugin plugin) {
        if (plugin == null) {
            return;
        }
        ClassLoader loader = plugin.getClass().getClassLoader();
        if (!keys.containsKey(loader)) {
            return;
        }
        keys.get(loader).unregisterAll();
    }

}
