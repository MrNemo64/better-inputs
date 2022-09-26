package me.nemo_64.betterinputs.bukkit.command.provider;

import me.lauriichan.laylib.command.Actor;
import me.lauriichan.laylib.command.IProviderArgumentType;
import me.nemo_64.betterinputs.bukkit.BetterInputsBukkit;

public final class BetterInputsProvider implements IProviderArgumentType<BetterInputsBukkit> {

    private final BetterInputsBukkit betterInputs;

    public BetterInputsProvider(final BetterInputsBukkit betterInputs) {
        this.betterInputs = betterInputs;
    }

    @Override
    public BetterInputsBukkit provide(Actor<?> actor) {
        return betterInputs;
    }

}
