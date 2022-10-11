package me.nemo_64.betterinputs.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

public final class BukkitPluginListener implements Listener {
    
    private final BetterInputsBukkit bukkit;
    
    BukkitPluginListener(BetterInputsBukkit bukkit) {
        this.bukkit = bukkit;
    }
    
    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        bukkit.unregister(event.getPlugin());
    }

}
