package me.nemo_64.betterinputs.nms;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import me.nemo_64.betterinputs.nms.packet.listener.PacketManager;

public abstract class VersionHandler {

    protected final Plugin plugin;
    protected final PlayerListener playerListener = new PlayerListener(this);

    protected final ConcurrentHashMap<UUID, PlayerAdapter> players = new ConcurrentHashMap<>();

    public VersionHandler(final Plugin plugin) {
        this.plugin = plugin;
    }

    public final Plugin getPlugin() {
        return plugin;
    }

    public final void enable() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        onEnable(pluginManager);
        pluginManager.registerEvents(playerListener, plugin);
        for (Player player : Bukkit.getOnlinePlayers()) {
            join(player);
        }
    }

    public final void disable() {
        HandlerList.unregisterAll(playerListener);
        for (Player player : Bukkit.getOnlinePlayers()) {
            quit(player);
        }
        onDisable();
    }

    protected void onEnable(final PluginManager pluginManager) {}

    protected void onDisable() {}

    public final PlayerAdapter getPlayer(UUID playerId) {
        if (players.containsKey(playerId)) {
            return players.get(playerId);
        }
        Player player = Bukkit.getPlayer(playerId);
        if (player == null) {
            return null;
        }
        PlayerAdapter adapter = createAdapter(player);
        players.put(playerId, adapter);
        return adapter;
    }

    final void join(Player player) {
        if (players.containsKey(player.getUniqueId())) {
            return;
        }
        createAdapter(player);
    }

    final void quit(Player player) {
        if (!players.containsKey(player.getUniqueId())) {
            return;
        }
        terminateAdapter(players.remove(player.getUniqueId()));
    }
    
    public abstract PacketManager getPacketManager();

    protected abstract PlayerAdapter createAdapter(Player player);

    protected abstract void terminateAdapter(PlayerAdapter adapter);

}
