package me.nemo_64.betterinputs.bukkit.message.impl;

import java.util.concurrent.ConcurrentHashMap;

import me.lauriichan.laylib.localization.MessageProvider;

public final class BetterMessageProvider extends MessageProvider {

    private final ConcurrentHashMap<String, BetterMessage> messages = new ConcurrentHashMap<>();
    private final String fallback;

    public BetterMessageProvider(String id, String fallback) {
        super(id);
        this.fallback = fallback;
    }

    @Override
    public BetterMessage getMessage(String language) {
        BetterMessage message = messages.get(language);
        if (message != null) {
            return message;
        }
        message = new BetterMessage(this, language);
        if (language != null && !language.isBlank()) {
            messages.put(language, message);
        }
        return message;
    }

    public String getFallback() {
        return fallback;
    }
    
}
