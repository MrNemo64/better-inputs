package me.nemo_64.betterinputs.api.input.modifier;

import java.util.Objects;

import me.nemo_64.betterinputs.api.input.InputProvider;
import me.nemo_64.betterinputs.api.util.tick.TickUnit;

public final class TimeoutModifier<T> extends AbstractModifier<T> {

    private long ticks = 0;

    /**
     * Creates a new {@code TimeoutModifier}
     * 
     * @param time the amount of time in the specified unit
     * @param unit the unit that time was specified as
     */
    public TimeoutModifier(long time, TickUnit unit) {
        super(true);
        Objects.requireNonNull(unit, "TickUnit can't be null");
        if (time <= 0) {
            throw new IllegalArgumentException("Long time has to be higher than 0");
        }
        this.ticks = unit.toTicks(time);
    }

    @Override
    public void tick() {
        if (ticks-- != 0) {
            return;
        }
        expire();
    }

    @Override
    public void onExpire(InputProvider<T> provider) {
        provider.cancel("Timeout");
    }

}
