package me.nemo_64.betterinputs.bukkit;

import java.util.Objects;

import org.bukkit.command.CommandSender;

import me.nemo_64.betterinputs.api.platform.IPlatformActor;
import me.nemo_64.betterinputs.api.platform.InvalidActor;

public final class BukkitActor<H extends CommandSender> implements IPlatformActor<H> {

    private final H handle;

    public BukkitActor(final H handle) {
        this.handle = Objects.requireNonNull(handle);
    }

    @Override
    public H getHandle() {
        return handle;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <P> IPlatformActor<P> as(Class<P> clazz) {
        if (clazz.isAssignableFrom(handle.getClass())) {
            return (IPlatformActor<P>) this;
        }
        return InvalidActor.get();
    }

}
