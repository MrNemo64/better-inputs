package me.nemo_64.betterinputs.api.input.modifier;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import me.nemo_64.betterinputs.api.input.InputProvider;
import me.nemo_64.betterinputs.api.platform.IPlatformActor;

public final class AttemptModifier<T> extends AbstractModifier<T> {

    private final Predicate<T> validator;
    private final Consumer<IPlatformActor<?>> messageSupplier;

    private int attempts = 0;

    /**
     * Creates a new {@code AttemptModifier}
     * 
     * @param attempts        number of allowed attempts
     * @param validator       the input validator
     * @param messageSupplier the "try again" message sender function
     */
    public AttemptModifier(int attempts, Predicate<T> validator, final Consumer<IPlatformActor<?>> messageSupplier) {
        super(true);
        if (attempts <= 0) {
            throw new IllegalArgumentException("Int attempts has to be higher than 0");
        }
        this.attempts = attempts;
        this.validator = validator;
        this.messageSupplier = Objects.requireNonNull(messageSupplier, "Consumer messageSupplier can't be null");
    }

    /**
     * Creates a new {@code AttemptModifier}
     * 
     * @param attempts  number of allowed attempts
     * @param validator the input validator
     */
    public AttemptModifier(int attempts, Predicate<T> validator) {
        this(attempts, validator, (actor) -> actor.sendMessage("Please try again"));
    }

    /**
     * Accepts input and validates it using the {@code valdiator} Predicate
     * 
     * @param  input the provided input
     * 
     * @return       if the attempt was valid or not
     */
    public boolean attempt(T input) {
        if (isExpired()) {
            return false;
        }
        if (attempts-- > 0) {
            if (validator.test(input)) {
                return true;
            }
            return false;
        }
        expire();
        return false;
    }

    /**
     * Sends a "try again" message to the {@code actor}
     * 
     * @param actor the actor that should receive the message
     */
    public final void sendMessage(IPlatformActor<?> actor) {
        messageSupplier.accept(actor);
    }

    @Override
    public void onExpire(InputProvider<T> provider) {
        provider.cancel("Too many attempts");
    }

}
