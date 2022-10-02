package me.nemo_64.betterinputs.bukkit.input.chat;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.nemo_64.betterinputs.api.input.modifier.AttemptModifier;

public final class ChatInputListener implements Listener {
    
    private final ConcurrentHashMap<UUID, ChatInput> map = new ConcurrentHashMap<>();
    
    @SuppressWarnings("unchecked")
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        if(!map.containsKey(id)) {
            return;
        }
        event.setCancelled(true);
        ChatInput input = map.get(id);
        String message = event.getMessage();
        AttemptModifier<String> modifier = (AttemptModifier<String>) input.provider().getModifier(AttemptModifier.class).orElse(null);
        if (modifier != null) {
            if (!modifier.attempt(message)) {
                modifier.sendMessage(input.provider().getActor());
                return;
            }
        }
        input.complete(message);
    }
    
    void removePlayer(UUID id)  {
        map.remove(id);
    }
    
    void addPlayer(UUID id, ChatInput input) {
        map.put(id, input);
    }

    boolean hasPlayer(UUID id) {
        return map.containsKey(id);
    }

}
