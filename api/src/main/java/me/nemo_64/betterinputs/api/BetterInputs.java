package me.nemo_64.betterinputs.api;

import java.util.Optional;

import me.nemo_64.betterinputs.api.platform.IPlatformKeyProvider;
import me.nemo_64.betterinputs.api.util.Ref;

public abstract class BetterInputs<PI> {

    private static final Ref<BetterInputs<?>> PLATFORM = Ref.of();

    public static BetterInputs<?> getPlatform() {
        return PLATFORM.get();
    }

    public BetterInputs() {
        if (PLATFORM.isPresent()) {
            throw new IllegalStateException("Platform is already registered");
        }
        PLATFORM.set(this).lock();
    }

    protected abstract PI asPlatformIdentifyable(Object object);

    public final Optional<IPlatformKeyProvider> tryGetKeyProvider(Object object) {
        PI identifyable = asPlatformIdentifyable(object);
        if (identifyable == null) {
            return Optional.empty();
        }
        return Optional.of(getKeyProvider(identifyable));
    }

    public abstract IPlatformKeyProvider getKeyProvider(PI platformIdentifyable);

}
