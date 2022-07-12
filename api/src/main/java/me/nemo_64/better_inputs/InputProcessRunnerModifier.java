package me.nemo_64.better_inputs;

import java.util.Optional;

/**
 * Interface that allows to alter the normal flow that an input process would follow. <br> For example, a
 * <i>TimerInputProcessRunnerModifier</i> could start a timer on the {@link #onStarted}, if the timer expires
 * before {@link #onFinish()} is called the input process fails with a custom {@link InputProcessFailureReason}
 * @param <I>
 * @param <V>
 */
public interface InputProcessRunnerModifier<I, V> {

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

    /*
    TODO Consider
        Should a modifier be able to edit the input and value? Or just be notified of it?
        For now we'll allow to modify the input and value but what does this really mean? If two modifiers
        edit the input/value which one do we use? Or do we have a priority system and the returned value
        is the one given to the next modifier? Do we allow a modifier to even discard an input/value? Like,
        if it returns an empty optional the input/value is discarded. Could allow some filtering based modifiers...
     */

    Optional<I> onInputReceived(I input);

    Optional<V> onValueConverted(V value);

}
