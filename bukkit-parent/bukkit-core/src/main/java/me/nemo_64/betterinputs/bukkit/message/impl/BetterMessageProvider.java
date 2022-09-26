package me.nemo_64.betterinputs.bukkit.message.impl;

import java.util.concurrent.ConcurrentHashMap;

import me.lauriichan.laylib.localization.MessageProvider;
import me.lauriichan.laylib.logger.ISimpleLogger;

public final class BetterMessageProvider extends MessageProvider {

    private final ConcurrentHashMap<String, BetterMessage> messages = new ConcurrentHashMap<>();
    private final String fallback;

    private final ISimpleLogger logger;

    public BetterMessageProvider(ISimpleLogger logger, String id, String fallback) {
        super(id);
        this.fallback = fallback;
        this.logger = logger;
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

    public ISimpleLogger getLogger() {
        return logger;
    }

    public String getFallback() {
        return fallback;
    }
    
}
