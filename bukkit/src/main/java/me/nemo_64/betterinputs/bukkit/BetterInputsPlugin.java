package me.nemo_64.betterinputs.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public final class BetterInputsPlugin extends JavaPlugin {

    private final BetterInputsBukkit api = new BetterInputsBukkit();

    @Override
    public void onEnable() {
        api.enable();
    }

    @Override
    public void onDisable() {
        api.disable();
    }

}
