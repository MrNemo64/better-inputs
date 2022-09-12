package me.nemo_64.betterinputs.api.input;

import java.util.Objects;
import java.util.function.Consumer;

import me.nemo_64.betterinputs.api.platform.IPlatformKey;
import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;
import me.nemo_64.betterinputs.api.util.argument.NotEnoughArgumentsException;
import me.nemo_64.betterinputs.api.util.registry.AbstractUnique;

public abstract class InputTypeProvider<V, I extends IInputType<V>> extends AbstractUnique {

    private final Class<V> inputType;

    public InputTypeProvider(final IPlatformKey key, final Class<V> inputType) {
        super(key);
        this.inputType = Objects.requireNonNull(inputType, "Class inputType can't be null");
    }

    public final Class<V> getInputType() {
        return inputType;
    }

    public final I build(final Consumer<ArgumentMap> consumer) throws NotEnoughArgumentsException {
        Objects.requireNonNull(consumer, "Consumer<ArgumentMap> can't be null!");
        ArgumentMap map = new ArgumentMap();
        consumer.accept(map);
        return build(map);
    }

    public final I build(final ArgumentMap map) throws NotEnoughArgumentsException {
        Objects.requireNonNull(map, "ArgumentMap can't be null!");
        I value = provide(map);
        map.throwIfMissing();
        return value;
    }

    protected abstract I provide(final ArgumentMap map);

}
