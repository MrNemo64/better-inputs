package me.nemo_64.betterinputs.bukkit.util.direction;

public enum Vertical {

    UP,
    UP_MID(2),
    MID,
    MID_DOWN(2),
    DOWN;

    private static final Vertical[] VALUES = values();

    private final int normalization;

    private Vertical() {
        this.normalization = ordinal();
    }

    private Vertical(int normalization) {
        this.normalization = normalization;
    }

    public Vertical normalize() {
        return VALUES[normalization];
    }

}
