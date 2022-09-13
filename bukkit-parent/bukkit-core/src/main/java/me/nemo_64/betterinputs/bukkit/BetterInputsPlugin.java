package me.nemo_64.betterinputs.bukkit;

import java.lang.reflect.Constructor;
import java.util.concurrent.ExecutorService;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import me.lauriichan.laylib.reflection.ClassUtil;
import me.lauriichan.laylib.reflection.JavaAccess;
import me.nemo_64.betterinputs.api.BetterInputs;
import me.nemo_64.betterinputs.api.platform.IPlatformKeyProvider;
import me.nemo_64.betterinputs.bukkit.input.anvil.AnvilInputFactory;
import me.nemo_64.betterinputs.bukkit.input.chat.ChatInputFactory;
import me.nemo_64.betterinputs.bukkit.input.command_block.CommandBlockInputFactory;
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

    @Override
    public void onLoad() {
        getServer().getServicesManager().register(BetterInputs.class, api, this, ServicePriority.Highest);
        try {
            versionHandler = initVersionHandler();
        } catch (Exception exp) {
            getLogger().severe("Failed to initialize version support");
            getLogger().severe("Reason: '" + exp.getMessage() + "'");
            getLogger().severe("");
            getLogger().severe("Some features might be disabled because of that");
        }
    }

    @Override
    public void onEnable() {
        this.keyProvider = api.getKeyProvider(this);
        // TODO: WIP
        // api.registerInputFactory(new ChatInputFactory(keyProvider.getKey("input/chat")));
        if (versionHandler != null) {
            // TODO: WIP
            // api.registerInputFactory(new AnvilInputFactory(keyProvider.getKey("input/anvil")));
            
            // TODO: Test if everything works correctly
            api.registerInputFactory(new CommandBlockInputFactory(keyProvider.getKey("input/command_block"), versionHandler));
        }
    }

    @Override
    public void onDisable() {
        if (versionHandler == null) {
            return;
        }
        api.shutdown();
    }

    public VersionHandler getVersionHandler() {
        return versionHandler;
    }

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
