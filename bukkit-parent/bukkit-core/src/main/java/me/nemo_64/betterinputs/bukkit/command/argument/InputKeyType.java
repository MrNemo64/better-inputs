package me.nemo_64.betterinputs.bukkit.command.argument;

import me.lauriichan.laylib.command.Actor;
import me.lauriichan.laylib.command.IArgumentType;
import me.nemo_64.betterinputs.api.BetterInputs;
import me.nemo_64.betterinputs.bukkit.command.model.InputKey;

public final class InputKeyType implements IArgumentType<InputKey> {

    private final BetterInputs<?> betterInputs = BetterInputs.getPlatform();

    @Override
    public InputKey parse(Actor<?> actor, String input) throws IllegalArgumentException {
        input = input.toLowerCase();
        if (!input.contains(":")) {
            input = "betterinputs:" + input;
        }
        if (!betterInputs.getKeys().contains(input)) {
            return null;
        }
        return new InputKey(input);
    }

}
