package me.nemo_64.betterinputs.api.input.modifier;

import me.nemo_64.betterinputs.api.input.InputProvider;

public abstract class AbstractModifier<T> {

    private final boolean ticking;
    private boolean expired = false;

    public AbstractModifier(final boolean ticking) {
        this.ticking = ticking;
    }

    /**
     * Gets if the modifier is a ticking modifier or not
     * 
     * @return if the modifier is ticking
     */
    public final boolean isTicking() {
        return ticking;
    }

    /**
     * Gets if the modifier is expired or not
     * 
     * @return if the modifier is expired
     */
    public final boolean isExpired() {
        return expired;
    }

    /**
     * Sets the modifier to be expired
     */
    protected final void expire() {
        this.expired = true;
    }

    /**
     * Is called every tick if the modifier is a ticking modifier
     */
    public void tick() {}

    /**
     * Is called once the modifier expires
     * 
     * @param provider the input provider that this modifier was set to
     */
    public void onExpire(InputProvider<T> provider) {}

    /**
     * Is called once the input process is done
     * 
     * @param provider the input provider that this modifier was set to
     */
    public void onDone(InputProvider<T> provider) {}

}
