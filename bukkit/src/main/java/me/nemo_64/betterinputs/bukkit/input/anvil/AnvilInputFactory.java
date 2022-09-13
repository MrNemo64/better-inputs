package me.nemo_64.betterinputs.bukkit.input.anvil;

import me.nemo_64.betterinputs.api.input.InputFactory;
import me.nemo_64.betterinputs.api.platform.IPlatformActor;
import me.nemo_64.betterinputs.api.platform.IPlatformKey;
import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;

public final class AnvilInputFactory extends InputFactory<String, AnvilInput> {

    public AnvilInputFactory(IPlatformKey key) {
        super(key, String.class);
    }

    @Override
    protected AnvilInput provide(IPlatformActor<?> actor, ArgumentMap map) {
        return null;
    }

}
