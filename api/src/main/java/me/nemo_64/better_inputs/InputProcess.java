package me.nemo_64.better_inputs;

import java.util.concurrent.CompletableFuture;

/**
 *
 * @param <S> Type of the input process sender
 * @param <V> Type of the value
 */
public interface InputProcess<S extends InputProcessSender<?>, V> {

    InputProcessRunner getRunner();

    S getSender();

    CompletableFuture<V> getValue();

    InputProcessState getState();

    InputProcessPriority getPriority();

}