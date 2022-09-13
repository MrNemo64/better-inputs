package me.nemo_64.betterinputs.bukkit;

import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import me.nemo_64.betterinputs.api.BetterInputs;
import me.nemo_64.betterinputs.api.platform.IPlatformKeyProvider;
import me.nemo_64.betterinputs.bukkit.input.anvil.AnvilInputFactory;
import me.nemo_64.betterinputs.bukkit.input.chat.ChatInputFactory;
import me.nemo_64.betterinputs.bukkit.input.command_block.CommandBlockInputFactory;

public final class BetterInputsPlugin extends JavaPlugin {

    private final BetterInputsBukkit api = new BetterInputsBukkit();

    private IPlatformKeyProvider keyProvider;

    @Override
    public void onLoad() {
        getServer().getServicesManager().register(BetterInputs.class, api, this, ServicePriority.Highest);
    }

    @Override
    public void onEnable() {
        this.keyProvider = api.getKeyProvider(this);
        api.registerInputFactory(new AnvilInputFactory(keyProvider.getKey("input/anvil")));
        api.registerInputFactory(new ChatInputFactory(keyProvider.getKey("input/chat")));
        api.registerInputFactory(new CommandBlockInputFactory(keyProvider.getKey("input/command_block")));
    }

    @Override
    public void onDisable() {
        api.shutdown();
    }

}
