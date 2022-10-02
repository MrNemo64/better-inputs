package me.nemo_64.betterinputs.bukkit;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.DrilldownPie;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import me.lauriichan.laylib.command.ArgumentRegistry;
import me.lauriichan.laylib.command.CommandManager;
import me.lauriichan.laylib.localization.MessageManager;
import me.lauriichan.laylib.localization.source.AnnotationMessageSource;
import me.lauriichan.laylib.localization.source.EnumMessageSource;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.laylib.reflection.ClassUtil;
import me.lauriichan.laylib.reflection.JavaAccess;
import me.nemo_64.betterinputs.api.BetterInputs;
import me.nemo_64.betterinputs.api.platform.IPlatformKeyProvider;
import me.nemo_64.betterinputs.bukkit.command.BetterInputsCommand;
import me.nemo_64.betterinputs.bukkit.command.argument.ArgumentMapType;
import me.nemo_64.betterinputs.bukkit.command.argument.InputKeyType;
import me.nemo_64.betterinputs.bukkit.command.impl.BukkitCommandInjector;
import me.nemo_64.betterinputs.bukkit.command.provider.BetterInputsProvider;
import me.nemo_64.betterinputs.bukkit.command.provider.LoggerProvider;
import me.nemo_64.betterinputs.bukkit.input.Inputs;
import me.nemo_64.betterinputs.bukkit.input.anvil.AnvilInputFactory;
import me.nemo_64.betterinputs.bukkit.input.chat.ChatInputFactory;
import me.nemo_64.betterinputs.bukkit.input.command_block.CommandBlockInputFactory;
import me.nemo_64.betterinputs.bukkit.message.*;
import me.nemo_64.betterinputs.bukkit.message.impl.BetterMessageProviderFactory;
import me.nemo_64.betterinputs.bukkit.util.BukkitExecutorService;
import me.nemo_64.betterinputs.bukkit.util.BukkitSimpleLogger;
import me.nemo_64.betterinputs.bukkit.nms.IServiceProvider;
import me.nemo_64.betterinputs.bukkit.nms.VersionHandler;

public final class BetterInputsPlugin extends JavaPlugin implements IServiceProvider {

    private static final String FORMAT = BetterInputsPlugin.class.getPackageName() + ".nms.%s.VersionHandler%s";

    private final BetterInputsBukkit api = new BetterInputsBukkit(this);

    private VersionHandler versionHandler;
    private String coreVersion;
    private IPlatformKeyProvider keyProvider;

    private final ExecutorService mainService = new BukkitExecutorService(this, false);
    private final ExecutorService asyncService = new BukkitExecutorService(this, true);

    private ISimpleLogger logger;
    private CommandManager commandManager;
    private MessageManager messageManager;

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicInteger>> statProviderType = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> statInputType = new ConcurrentHashMap<>();
    private final AtomicInteger statCreatedInput = new AtomicInteger(0);

    /*
     * Setup
     */

    @Override
    public void onLoad() {
        getServer().getServicesManager().register(BetterInputs.class, api, this, ServicePriority.Highest);
        setupVersionHandler();
        setupEnvironment();
    }

    private void setupEnvironment() {
        logger = new BukkitSimpleLogger(getLogger());
        messageManager = new MessageManager();
        commandManager = new CommandManager(logger);
    }

    private void setupVersionHandler() {
        try {
            versionHandler = initVersionHandler();
        } catch (Exception exp) {
            getLogger().severe("Failed to initialize version support");
            getLogger().severe("Reason: '" + exp.getMessage() + "'");
            getLogger().severe("");
            getLogger().severe("Some features might be disabled because of that");
        }
    }

    /*
     * Start
     */

    @Override
    public void onEnable() {
        setupMetrics();
        commandManager.setInjector(new BukkitCommandInjector(commandManager, messageManager, this));
        keyProvider = api.getKeyProvider(this);
        registerMessages(messageManager);
        registerArgumentTypes(commandManager.getRegistry());
        registerCommands(commandManager);
        registerListeners(getServer().getPluginManager());
        registerInputFactories();
        if (versionHandler != null) {
            versionHandler.enable();
        }
    }

    private void registerMessages(MessageManager manager) {
        final BetterMessageProviderFactory factory = new BetterMessageProviderFactory();
        manager.register(new EnumMessageSource(CommandManagerMessage.class, factory));
        manager.register(new EnumMessageSource(CommandDescription.class, factory));
        manager.register(new AnnotationMessageSource(Messages.class, factory));
    }

    private void registerArgumentTypes(ArgumentRegistry registry) {
        // Register argument types
        registry.registerArgumentType(InputKeyType.class);
        registry.registerArgumentType(ArgumentMapType.class);

        // Register providers
        registry.setProvider(new BetterInputsProvider(api));
        registry.setProvider(new LoggerProvider(logger));
    }

    private void registerCommands(CommandManager manager) {
        manager.register(BetterInputsCommand.class);
    }

    private void registerListeners(PluginManager pluginManager) {
        pluginManager.registerEvents(new BukkitPluginListener(api), this);
    }

    private void registerInputFactories() {
        api.registerInputFactory(new ChatInputFactory(keyProvider.getKey(Inputs.CHAT.substring(13)), this));
        if (versionHandler != null) {
            api.registerInputFactory(new AnvilInputFactory(keyProvider.getKey(Inputs.ANVIL.substring(13)), versionHandler));
            api.registerInputFactory(new CommandBlockInputFactory(keyProvider.getKey(Inputs.COMMAND_BLOCK.substring(13)), versionHandler));
        }
    }

    private void setupMetrics() {
        Metrics metrics = new Metrics(this, 16546);
        metrics.addCustomChart(new SingleLineChart("input_created", () -> {
            return statCreatedInput.getAndSet(0);
        }));
        metrics.addCustomChart(new AdvancedPie("default_provider_type", () -> {
            HashMap<String, Integer> map = new HashMap<>();
            Map<String, AtomicInteger> providedMap = statProviderType.get(keyProvider.getNamespace());
            for (String key : providedMap.keySet()) {
                String name = key.substring(6);
                map.put(name, providedMap.get(key).get());
            }
            return map;
        }));
        metrics.addCustomChart(new DrilldownPie("plugin_providers", () -> {
            HashMap<String, Map<String, Integer>> allMap = new HashMap<>();
            for (String key : statProviderType.keySet()) {
                HashMap<String, Integer> map = new HashMap<>();
                Map<String, AtomicInteger> providedMap = statProviderType.get(key);
                for (String providedKey : providedMap.keySet()) {
                    map.put(providedKey, providedMap.get(providedKey).get());
                }
                allMap.put(key, map);
            }
            statProviderType.clear();
            return allMap;
        }));
        metrics.addCustomChart(new AdvancedPie("input_type", () -> {
            HashMap<String, Integer> map = new HashMap<>();
            for (String key : statInputType.keySet()) {
                map.put(key, statInputType.get(key).get());
            }
            statInputType.clear();
            return map;
        }));
        metrics.addCustomChart(new SimplePie("core_version", () -> {
            return coreVersion;
        }));
    }

    /*
     * Shutdown
     */

    @Override
    public void onDisable() {
        if (versionHandler != null) {
            versionHandler.disable();
        }
        api.shutdown();
    }

    /*
     * Stats
     */

    final void updateStat(String providerType, Class<?> inputType) {
        statCreatedInput.addAndGet(1);
        int index = providerType.indexOf(':');
        String namespace = providerType.substring(0, index);
        String key = providerType.substring(index + 1, providerType.length());
        statProviderType.computeIfAbsent(namespace, (i) -> new ConcurrentHashMap<>()).computeIfAbsent(key, (i) -> new AtomicInteger(0))
            .addAndGet(1);
        String inputName = inputType.getName();
        if (inputName.startsWith("java.lang.")) {
            inputName = inputType.getSimpleName();
        }
        statInputType.computeIfAbsent(inputName, (i) -> new AtomicInteger(0)).addAndGet(1);
    }

    /*
     * Getter
     */

    public VersionHandler getVersionHandler() {
        return versionHandler;
    }

    /*
     * ServiceProvider implementation
     */

    @Override
    public final Plugin plugin() {
        return this;
    }

    @Override
    public final ExecutorService mainService() {
        return mainService;
    }

    @Override
    public final ExecutorService asyncService() {
        return asyncService;
    }

    /*
     * Init Version Handler
     */

    private final VersionHandler initVersionHandler() {
        String version = getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        String path = String.format(FORMAT, version, (coreVersion = version.substring(1)));
        Class<?> clazz = ClassUtil.findClass(path);
        if (clazz == null || !VersionHandler.class.isAssignableFrom(clazz)) {
            throw new IllegalStateException("Couldn't find class '" + path + "'!");
        }
        Constructor<?> constructor = ClassUtil.getConstructor(clazz, IServiceProvider.class);
        if (constructor == null) {
            throw new IllegalStateException("Couldn't find valid constructor for class '" + path + "'!");
        }
        return (VersionHandler) JavaAccess.instance(constructor, this);
    }

}
