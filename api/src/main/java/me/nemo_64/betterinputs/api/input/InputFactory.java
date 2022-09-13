package me.nemo_64.betterinputs.api.input;

import java.util.Objects;

import me.nemo_64.betterinputs.api.platform.IPlatformActor;
import me.nemo_64.betterinputs.api.platform.IPlatformKey;
import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;
import me.nemo_64.betterinputs.api.util.argument.NotEnoughArgumentsException;
import me.nemo_64.betterinputs.api.util.registry.AbstractUnique;

public abstract class InputFactory<V, I extends AbstractInput<V>> extends AbstractUnique {

    private final Class<V> inputType;

    public InputFactory(final IPlatformKey key, final Class<V> inputType) {
        super(key);
        this.inputType = Objects.requireNonNull(inputType, "Class inputType can't be null");
    }

    public final Class<V> getInputType() {
        return inputType;
    }

    public final I build(final IPlatformActor<?> actor, final ArgumentMap map) throws NotEnoughArgumentsException {
        Objects.requireNonNull(map, "ArgumentMap can't be null!");
        I value = provide(actor, map);
        map.throwIfMissing();
        return value;
    }

    public void onUnregister() {}

    protected abstract I provide(final IPlatformActor<?> actor, final ArgumentMap map);

}
