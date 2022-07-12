package me.nemo_64.better_inputs;

import java.util.Optional;

public interface InputProcessManager {

    int estimateQueuedProcessesFor(InputProcessSender<?> sender);

    boolean isRunningAProcess(InputProcessSender<?> sender);

    boolean unqueueProcess(InputProcess<?, ?> process);

    boolean queueProcess(InputProcess<?, ?> process);

    Optional<InputProcessRunner<?>> getRunningProcessFor(InputProcessSender<?> sender);

}
