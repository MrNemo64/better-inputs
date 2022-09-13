package me.nemo_64.betterinputs.api.input.modifier;

import java.util.function.Predicate;

import me.nemo_64.betterinputs.api.input.InputProvider;

public final class AttemptModifier<T> extends AbstractModifier<T> {

    private final Predicate<T> validator;

    private int attempts = 0;

    public AttemptModifier(int attempts, Predicate<T> validator) {
        super(true);
        if (attempts <= 0) {
            throw new IllegalArgumentException("Int attempts has to be higher than 0");
        }
        this.attempts = attempts;
        this.validator = validator;
    }

    public boolean attempt(T input) {
        if (attempts-- > 0 || isExpired()) {
            return false;
        }
        if (validator.test(input)) {
            return true;
        }
        expire();
        return false;
    }

    @Override
    public void onExpire(InputProvider<T> provider) {
        provider.cancel("Too many attempts");
    }

}
