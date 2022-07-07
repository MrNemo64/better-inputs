package me.nemo_64.better_inputs;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @param <S> Type of the input process sender
 * @param <V> Type of the value
 */
public interface InputProcess<S extends InputProcessSender<?>, V> {

    boolean queue(InputProcessManager manager);

    boolean cancel();

    default boolean isRunning() {
        return getState() == InputProcessState.RUNNING;
    }

    default boolean isFinished() {
        return getState() == InputProcessState.FINISHED;
    }

    S getSender();

    Optional<InputProcessManager> getManager();

    CompletableFuture<V> getValue();

    InputProcessState getState();

    InputProcessPriority getPriority();

}