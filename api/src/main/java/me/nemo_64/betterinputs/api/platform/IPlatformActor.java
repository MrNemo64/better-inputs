package me.nemo_64.betterinputs.api.platform;

public interface IPlatformActor<H> {

    default boolean isAvailable() {
        return true;
    }

    H getHandle();

    <P> IPlatformActor<P> as(Class<P> clazz);

}
