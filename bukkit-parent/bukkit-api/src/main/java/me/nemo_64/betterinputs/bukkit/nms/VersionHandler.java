package me.nemo_64.betterinputs.bukkit.nms;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import me.nemo_64.betterinputs.bukkit.nms.packet.listener.PacketManager;

public abstract class VersionHandler {

    protected final PlayerListener playerListener = new PlayerListener(this);

    protected final ConcurrentHashMap<UUID, PlayerAdapter> players = new ConcurrentHashMap<>();

    protected final IServiceProvider provider;

    public VersionHandler(final IServiceProvider provider) {
        this.provider = provider;
    }

    public final void enable() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        onEnable(pluginManager);
        pluginManager.registerEvents(playerListener, provider.plugin());
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

    public final PlayerAdapter getPlayer(Player player) {
        if (player == null) {
            return null;
        }
        UUID playerId = player.getUniqueId();
        if (players.containsKey(playerId)) {
            return players.get(playerId);
        }
        PlayerAdapter adapter = createAdapter(player);
        players.put(playerId, adapter);
        return adapter;
    }

    final void join(Player player) {
        if (players.containsKey(player.getUniqueId())) {
            return;
        }
        players.put(player.getUniqueId(), createAdapter(player));
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

    public final Plugin plugin() {
        return provider.plugin();
    }

    public final ExecutorService mainService() {
        return provider.mainService();
    }

    public final ExecutorService asyncService() {
        return provider.asyncService();
    }

}
