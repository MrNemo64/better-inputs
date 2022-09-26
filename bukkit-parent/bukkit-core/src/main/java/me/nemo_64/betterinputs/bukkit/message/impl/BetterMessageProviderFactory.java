package me.nemo_64.betterinputs.bukkit.message.impl;

import me.lauriichan.laylib.localization.source.IProviderFactory;
import me.lauriichan.laylib.logger.ISimpleLogger;

public final class BetterMessageProviderFactory implements IProviderFactory {

    private final ISimpleLogger logger;

    public BetterMessageProviderFactory(final ISimpleLogger logger) {
        this.logger = logger;
    }

    @Override
    public BetterMessageProvider build(String id, String fallback) {
        return new BetterMessageProvider(logger, id, fallback);
    }

}
