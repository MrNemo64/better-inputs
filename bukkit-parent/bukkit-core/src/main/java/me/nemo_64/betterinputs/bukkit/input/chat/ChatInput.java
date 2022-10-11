package me.nemo_64.betterinputs.bukkit.input.chat;

import org.bukkit.entity.Player;

import me.nemo_64.betterinputs.api.input.AbstractInput;
import me.nemo_64.betterinputs.api.input.InputProvider;
import me.nemo_64.betterinputs.api.platform.IPlatformActor;

public final class ChatInput extends AbstractInput<String> {

    private final ChatInputListener listener;
    
    public ChatInput(ChatInputListener listener) {
        this.listener = listener;
    }
    
    @Override
    protected void onStart(InputProvider<String> provider, IPlatformActor<?> actor) {
        Player bukkitPlayer = actor.as(Player.class).getHandle();
        if (bukkitPlayer == null) {
            throw new IllegalStateException("Unsupported actor");
        }
        if(listener.hasPlayer(bukkitPlayer.getUniqueId())) {
            throw new IllegalStateException("There is already a chat input running for this actor");
        }
        listener.addPlayer(bukkitPlayer.getUniqueId(), this);
    }

    void complete(String message) {
        completeValue(message);
    }

    @Override
    protected boolean onCancel() {
        listener.removePlayer(provider().getActor().as(Player.class).getHandle().getUniqueId());
        return true;
    }

}
