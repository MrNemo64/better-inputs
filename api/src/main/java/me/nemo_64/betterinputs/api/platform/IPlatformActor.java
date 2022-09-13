package me.nemo_64.betterinputs.api.platform;

public interface IPlatformActor<H> {
    
    H getHandle();
    
    <P> IPlatformActor<P> as(Class<P> clazz);

}
