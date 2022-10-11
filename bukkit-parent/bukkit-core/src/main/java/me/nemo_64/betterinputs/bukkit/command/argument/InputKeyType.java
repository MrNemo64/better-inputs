package me.nemo_64.betterinputs.bukkit.command.argument;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import me.lauriichan.laylib.command.Actor;
import me.lauriichan.laylib.command.IArgumentType;
import me.lauriichan.laylib.command.Suggestions;
import me.lauriichan.laylib.command.util.LevenshteinDistance;
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
            throw new IllegalArgumentException("Unknown input type '" + input + "'!");
        }
        return new InputKey(input);
    }

    @Override
    public void suggest(Actor<?> actor, String input, Suggestions suggestions) {
        List<Entry<String, Integer>> list = LevenshteinDistance.rankByDistance(input, betterInputs.getKeys());
        double max = list.stream().map(Entry::getValue).collect(Collectors.summingInt(Integer::intValue));
        for (int index = 0; index < list.size(); index++) {
            Entry<String, Integer> entry = list.get(index);
            suggestions.suggest(1 - (entry.getValue().doubleValue() / max), entry.getKey());
        }
    }

}
