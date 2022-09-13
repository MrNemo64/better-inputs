package me.nemo_64.betterinputs.api;

import java.util.Objects;
import java.util.Optional;

import me.nemo_64.betterinputs.api.input.AbstractInput;
import me.nemo_64.betterinputs.api.input.InputBuilder;
import me.nemo_64.betterinputs.api.input.InputFactory;
import me.nemo_64.betterinputs.api.input.InputProvider;
import me.nemo_64.betterinputs.api.platform.IPlatformActor;
import me.nemo_64.betterinputs.api.platform.IPlatformKeyProvider;
import me.nemo_64.betterinputs.api.util.Ref;
import me.nemo_64.betterinputs.api.util.argument.NotEnoughArgumentsException;
import me.nemo_64.betterinputs.api.util.tick.TickTimer;

public abstract class BetterInputs<P> {

    private static final Ref<BetterInputs<?>> PLATFORM = Ref.of();

    public static BetterInputs<?> getPlatform() {
        return PLATFORM.get();
    }

    private final TickTimer inputTick = new TickTimer("InputTick");

    public BetterInputs() {
        if (PLATFORM.isPresent()) {
            throw new IllegalStateException("Platform is already registered");
        }
        PLATFORM.set(this).lock();
    }

    protected abstract P asPlatformIdentifyable(Object object);

    public final Optional<IPlatformKeyProvider> tryGetKeyProvider(Object object) {
        P identifyable = asPlatformIdentifyable(object);
        if (identifyable == null) {
            return Optional.empty();
        }
        return Optional.of(getKeyProvider(identifyable));
    }

    public abstract IPlatformKeyProvider getKeyProvider(P platformIdentifyable);

    public abstract <E> Optional<IPlatformActor<E>> getActor(E actor);

    public abstract <T> Optional<InputFactory<T, ? extends AbstractInput<T>>> getInputFactory(String namespacedKey);

    public final <T> InputBuilder<T> createInput(Class<T> type) {
        return new InputBuilder<>(this, type);
    }

    public final <T> InputProvider<T> createProvider(InputBuilder<T> builder)
        throws NullPointerException, IllegalArgumentException, NotEnoughArgumentsException {
        Objects.requireNonNull(builder, "InputBuilder can't be null");
        Objects.requireNonNull(builder.actor(), "Actor can't be null (Maybe invalid actor?)");
        InputFactory<T, ? extends AbstractInput<T>> factory = this.<T>getInputFactory(builder.type())
            .orElseThrow(() -> new IllegalArgumentException("Invalid InputFactory type '" + builder.type() + "'!"));
        return new InputProvider<>(inputTick, factory.build(builder.actor(), builder.argumentMap()), builder.exceptionHandler(),
            builder.cancelListener());
    }

}
