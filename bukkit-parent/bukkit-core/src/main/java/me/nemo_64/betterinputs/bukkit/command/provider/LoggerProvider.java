package me.nemo_64.betterinputs.bukkit.command.provider;

import me.lauriichan.laylib.command.Actor;
import me.lauriichan.laylib.command.IProviderArgumentType;
import me.lauriichan.laylib.logger.ISimpleLogger;

public final class LoggerProvider implements IProviderArgumentType<ISimpleLogger> {

    private final ISimpleLogger logger;

    public LoggerProvider(final ISimpleLogger logger) {
        this.logger = logger;
    }

    @Override
    public ISimpleLogger provide(Actor<?> actor) {
        return logger;
    }

}
