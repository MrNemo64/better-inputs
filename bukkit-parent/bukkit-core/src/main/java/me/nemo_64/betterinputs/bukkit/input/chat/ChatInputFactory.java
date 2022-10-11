package me.nemo_64.betterinputs.bukkit.input.chat;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import me.nemo_64.betterinputs.api.input.InputFactory;
import me.nemo_64.betterinputs.api.platform.IPlatformActor;
import me.nemo_64.betterinputs.api.platform.IPlatformKey;
import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;

public final class ChatInputFactory extends InputFactory<String, ChatInput> {

    private final ChatInputListener listener;

    public ChatInputFactory(IPlatformKey key, Plugin plugin) {
        super(key, String.class);
        this.listener = new ChatInputListener();
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public void onUnregister() {
        HandlerList.unregisterAll(listener);
    }

    @Override
    protected ChatInput provide(IPlatformActor<?> actor, ArgumentMap map) {
        return new ChatInput(listener);
    }

}
