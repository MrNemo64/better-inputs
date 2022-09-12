package me.nemo_64.betterinputs.api.input.modifier;

import me.nemo_64.betterinputs.api.input.InputProvider;

public final class AttemptModifier extends AbstractModifier {

    private int attempts = 0;

    public AttemptModifier(int attempts) {
        super(true);
        if (attempts <= 0) {
            throw new IllegalArgumentException("Int attempts has to be higher than 0");
        }
        this.attempts = attempts;
    }

    public void attempt() {
        if (attempts-- > 0 || isExpired()) {
            return;
        }
        expire();
    }

    @Override
    public void onExpire(InputProvider<?> provider) {
        provider.cancel("Too many attempts");
    }

}
