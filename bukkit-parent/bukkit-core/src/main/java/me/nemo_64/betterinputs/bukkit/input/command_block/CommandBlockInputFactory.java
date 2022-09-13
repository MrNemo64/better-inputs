package me.nemo_64.betterinputs.bukkit.input.command_block;

import me.nemo_64.betterinputs.api.input.InputFactory;
import me.nemo_64.betterinputs.api.platform.IPlatformActor;
import me.nemo_64.betterinputs.api.platform.IPlatformKey;
import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;

public final class CommandBlockInputFactory extends InputFactory<String, CommandBlockInput> {

    public CommandBlockInputFactory(IPlatformKey key) {
        super(key, String.class);
    }

    @Override
    protected CommandBlockInput provide(IPlatformActor<?> actor, ArgumentMap map) {
        return null;
    }

}
