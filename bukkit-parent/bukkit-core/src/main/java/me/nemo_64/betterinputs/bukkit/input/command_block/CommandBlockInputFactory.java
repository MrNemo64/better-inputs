package me.nemo_64.betterinputs.bukkit.input.command_block;

import me.nemo_64.betterinputs.api.input.InputFactory;
import me.nemo_64.betterinputs.api.platform.IPlatformActor;
import me.nemo_64.betterinputs.api.platform.IPlatformKey;
import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;
import me.nemo_64.betterinputs.bukkit.nms.VersionHandler;

public final class CommandBlockInputFactory extends InputFactory<String, CommandBlockInput> {

    private final VersionHandler versionHandler;
    private final CommandBlockPacketListener listener;

    public CommandBlockInputFactory(IPlatformKey key, VersionHandler versionHandler) {
        super(key, String.class);
        this.versionHandler = versionHandler;
        this.listener = new CommandBlockPacketListener(versionHandler.getPacketManager());
    }

    @Override
    public void onUnregister() { // This can't be reregistered rn; new Factory required
        versionHandler.getPacketManager().unregister(listener.getContainer());
    }

    @Override
    protected CommandBlockInput provide(IPlatformActor<?> actor, ArgumentMap map) {
        return new CommandBlockInput(versionHandler);
    }

}
