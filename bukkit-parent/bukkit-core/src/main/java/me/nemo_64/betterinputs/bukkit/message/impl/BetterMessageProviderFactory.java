package me.nemo_64.betterinputs.bukkit.message.impl;

import me.lauriichan.laylib.localization.source.IProviderFactory;

public final class BetterMessageProviderFactory implements IProviderFactory {

    @Override
    public BetterMessageProvider build(String id, String fallback) {
        return new BetterMessageProvider(id, fallback);
    }

}
