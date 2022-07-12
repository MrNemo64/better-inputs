package me.nemo_64.better_inputs;

import java.util.Collection;

public interface InputProcessRunner<P extends InputProcess<?, ?>> {

    P getInputProcess();

    void startProcess();

    void cancelProcess(InputProcessFailureReason reason);

    boolean isRunning();

    Collection<InputProcessRunnerModifier> getModifiers();

}
