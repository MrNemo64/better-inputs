package me.nemo_64.betterinputs.api.input.modifier;

import java.util.Objects;

import me.nemo_64.betterinputs.api.input.InputProvider;
import me.nemo_64.betterinputs.api.util.tick.TickUnit;

public final class TimeoutModifier extends AbstractModifier {

    private long ticks = 0;

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
    public void onExpire(InputProvider<?> provider) {
        provider.cancel("Timeout");
    }

}
