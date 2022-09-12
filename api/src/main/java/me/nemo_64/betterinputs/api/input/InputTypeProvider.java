package me.nemo_64.betterinputs.api.input;

import me.nemo_64.betterinputs.api.platform.IPlatformKey;
import me.nemo_64.betterinputs.api.util.argument.IArgumentMap;
import me.nemo_64.betterinputs.api.util.registry.AbstractUnique;

public abstract class InputTypeProvider<I extends IInputType> extends AbstractUnique {

    public InputTypeProvider(final IPlatformKey key) {
        super(key);
    }

    public abstract I provide(final IArgumentMap map);
    
}
