package me.nemo_64.betterinputs.bukkit.message;

import java.util.Arrays;
import java.util.stream.Collectors;

import me.lauriichan.laylib.localization.source.IMessageDefinition;

public enum CommandDescription implements IMessageDefinition {

    ;

    private final String id;
    private final String fallback;

    private CommandDescription() {
        this("");
    }

    private CommandDescription(String[] fallback) {
        this(Arrays.stream(fallback).collect(Collectors.joining("\n")));
    }

    private CommandDescription(String fallback) {
        this.id = "command.description." + name().replace('$', '-').toLowerCase().replace('_', '.');
        this.fallback = fallback;
    }

    @Override
    public String fallback() {
        return fallback;
    }

    @Override
    public String id() {
        return id;
    }

}
