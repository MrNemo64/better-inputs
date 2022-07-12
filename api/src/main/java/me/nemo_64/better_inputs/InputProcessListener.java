package me.nemo_64.better_inputs;

import java.util.function.Consumer;

public interface InputProcessListener<I> {

    /*
    TODO Consider
        InputProcessRunnerModifier also needs to be notified when the process is queued, starts, etc
        Should we have another interface with the onQueued onStarted onFinish and onCancel methods?
     */

    /**
     * Called when in associated {@link InputProcess} is queued
     */
    void onQueued();

    /**
     * Called when in associated {@link InputProcess} is stared
     */
    void onStarted();

    /**
     * Called when in associated {@link InputProcess} finishes successfully
     */
    void onFinish();

    /**
     * Called when in associated {@link InputProcess} is cancelled
     */
    void onCancel();

    /**
     * Method to link the {@link InputProcessListener} and the {@link InputProcess}.
     * <br><b>Should only be called ones when the {@link InputProcess} is created, calling this method in other moment
     * or more than ones may result in a {@link RuntimeException} depending on the implementation</b>
     * @param onInputReceived
     * @param failInputProcess
     */
    void linkInputProcess(Consumer<I> onInputReceived, Consumer<? extends InputProcessFailureReason> failInputProcess, InputProcess<?, ?> process);

}