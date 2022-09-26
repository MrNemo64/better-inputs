package me.nemo_64.betterinputs.bukkit;

import java.lang.reflect.Constructor;
import java.util.concurrent.ExecutorService;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import me.lauriichan.laylib.command.ArgumentRegistry;
import me.lauriichan.laylib.command.CommandManager;
import me.lauriichan.laylib.localization.MessageManager;
import me.lauriichan.laylib.localization.source.AnnotationMessageSource;
import me.lauriichan.laylib.localization.source.EnumMessageSource;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.laylib.logger.JavaSimpleLogger;
import me.lauriichan.laylib.reflection.ClassUtil;
import me.lauriichan.laylib.reflection.JavaAccess;
import me.nemo_64.betterinputs.api.BetterInputs;
import me.nemo_64.betterinputs.api.platform.IPlatformKeyProvider;
import me.nemo_64.betterinputs.bukkit.command.BetterInputsCommand;
import me.nemo_64.betterinputs.bukkit.command.argument.ArgumentMapType;
import me.nemo_64.betterinputs.bukkit.command.argument.InputKeyType;
import me.nemo_64.betterinputs.bukkit.command.impl.BukkitCommandInjector;
import me.nemo_64.betterinputs.bukkit.command.provider.BetterInputsProvider;
// import me.nemo_64.betterinputs.bukkit.input.anvil.AnvilInputFactory;
// import me.nemo_64.betterinputs.bukkit.input.chat.ChatInputFactory;
import me.nemo_64.betterinputs.bukkit.input.command_block.CommandBlockInputFactory;
import me.nemo_64.betterinputs.bukkit.message.*;
import me.nemo_64.betterinputs.bukkit.message.impl.BetterMessageProviderFactory;
import me.nemo_64.betterinputs.bukkit.util.BukkitExecutorService;
import me.nemo_64.betterinputs.bukkit.nms.IServiceProvider;
import me.nemo_64.betterinputs.bukkit.nms.VersionHandler;

public final class BetterInputsPlugin extends JavaPlugin implements IServiceProvider {

    private static final String FORMAT = BetterInputsPlugin.class.getPackageName() + ".nms.%s.VersionHandler%s";

    private final BetterInputsBukkit api = new BetterInputsBukkit();

    private VersionHandler versionHandler;
    private IPlatformKeyProvider keyProvider;

    private final ExecutorService mainService = new BukkitExecutorService(this, false);
    private final ExecutorService asyncService = new BukkitExecutorService(this, true);

    private ISimpleLogger logger;
    private CommandManager commandManager;
    private MessageManager messageManager;

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
        logger = new JavaSimpleLogger(getLogger());
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
        commandManager.setInjector(new BukkitCommandInjector(commandManager, messageManager, this));
        keyProvider = api.getKeyProvider(this);
        registerMessages(messageManager);
        registerArgumentTypes(commandManager.getRegistry());
        registerCommands(commandManager);
        registerInputFactories();
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
    }

    private void registerCommands(CommandManager manager) {
        manager.register(BetterInputsCommand.class);
    }

    private void registerInputFactories() {
        // TODO: WIP
        // api.registerInputFactory(new ChatInputFactory(keyProvider.getKey("input/chat")));
        if (versionHandler != null) {
            // TODO: WIP
            // api.registerInputFactory(new AnvilInputFactory(keyProvider.getKey("input/anvil")));

            // TODO: Test if everything works correctly
            api.registerInputFactory(new CommandBlockInputFactory(keyProvider.getKey("input/command_block"), versionHandler));
        }
    }

    /*
     * Shutdown
     */

    @Override
    public void onDisable() {
        if (versionHandler == null) {
            return;
        }
        api.shutdown();
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
        String path = String.format(FORMAT, version, version.substring(1));
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
