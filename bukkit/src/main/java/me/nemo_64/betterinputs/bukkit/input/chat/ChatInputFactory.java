package me.nemo_64.betterinputs.bukkit.input.chat;

import me.nemo_64.betterinputs.api.input.InputFactory;
import me.nemo_64.betterinputs.api.platform.IPlatformActor;
import me.nemo_64.betterinputs.api.platform.IPlatformKey;
import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;

public final class ChatInputFactory extends InputFactory<String, ChatInput> {

    public ChatInputFactory(IPlatformKey key) {
        super(key, String.class);
    }

    @Override
    protected ChatInput provide(IPlatformActor<?> actor, ArgumentMap map) {
        return null;
    }

}
