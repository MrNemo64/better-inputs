package me.nemo_64.betterinputs.api.input;

import java.util.Objects;
import java.util.function.Consumer;

import me.nemo_64.betterinputs.api.platform.IPlatformKey;
import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;
import me.nemo_64.betterinputs.api.util.registry.AbstractUnique;

public abstract class InputTypeProvider<I extends IInputType> extends AbstractUnique {

    public InputTypeProvider(final IPlatformKey key) {
        super(key);
    }

    public final I build(final Consumer<ArgumentMap> consumer) {
        Objects.requireNonNull(consumer, "Consumer<ArgumentMap> can't be null!");
        ArgumentMap map = new ArgumentMap();
        consumer.accept(map);
        return build(map);
    }

    public final I build(final ArgumentMap map) {
        Objects.requireNonNull(map, "ArgumentMap can't be null!");
        I value = provide(map);
        map.throwIfMissing();
        return value;
    }

    protected abstract I provide(final ArgumentMap map);

}
