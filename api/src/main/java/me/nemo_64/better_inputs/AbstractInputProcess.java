package me.nemo_64.better_inputs;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractInputProcess<S extends InputProcessSender<?>, I, V> implements InputProcess<S, V> {

    protected final CompletableFuture<V> value = new CompletableFuture<>();

    protected final S sender;
    protected final InputProcessListener<I> listener;
    protected final InputProcessPriority processPriority;

    protected InputProcessState state = InputProcessState.CREATED;

    protected AbstractInputProcess(S sender, InputProcessListener<I> listener, InputProcessPriority processPriority) {
        this.listener = Objects.requireNonNull(listener);
        this.sender = Objects.requireNonNull(sender);
        this.processPriority = processPriority == null ? InputProcessPriority.NORMAL : processPriority;

        linkInputProcessListener();
    }

    /**
     * Code to run when the input process starts
     */
    protected abstract void onStart();

    protected void linkInputProcessListener() {
        this.listener.linkInputProcess(this::acceptInput, this::failProcess, this);
    }

    protected void acceptInput(I input) {
        // TODO
    }

    protected void failProcess(InputProcessFailureReason reason) {
        if (reason == null)
            reason = InputProcessFailureReason.UNKNOWN;
        // TODO
    }

    @Override
    public S getSender() {
        return sender;
    }

    @Override
    public CompletableFuture<V> getValue() {
        return value;
    }

    @Override
    public InputProcessState getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return getSender().hashCode();
    }
}
