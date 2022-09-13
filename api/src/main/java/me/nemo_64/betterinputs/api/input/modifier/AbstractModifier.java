package me.nemo_64.betterinputs.api.input.modifier;

import me.nemo_64.betterinputs.api.input.InputProvider;

public abstract class AbstractModifier<T> {

    private final boolean ticking;
    private boolean expired = false;

    public AbstractModifier(final boolean ticking) {
        this.ticking = ticking;
    }

    public final boolean isTicking() {
        return ticking;
    }

    public final boolean isExpired() {
        return expired;
    }

    protected final void expire() {
        this.expired = true;
    }

    public void tick() {}

    public void onExpire(InputProvider<T> provider) {}
    
    public void onDone(InputProvider<T> provider) {}

}
