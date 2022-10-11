package me.nemo_64.betterinputs.bukkit.command.model;

public final class InputKey {

    private final String namespacedKey;

    public InputKey(final String namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    public final String namespacedKey() {
        return namespacedKey;
    }

}
