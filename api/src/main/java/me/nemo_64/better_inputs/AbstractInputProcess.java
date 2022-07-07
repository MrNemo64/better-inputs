package me.nemo_64.better_inputs;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractInputProcess<S extends InputProcessSender<?>, I, V> implements InputProcess<S, V> {

    protected final CompletableFuture<V> value = new CompletableFuture<>();

    protected final S sender;
    protected final InputProcessListener<I> listener;
    protected final InputProcessPriority processPriority;

    protected InputProcessState state = InputProcessState.CREATED;
    protected Optional<InputProcessManager> manager = Optional.empty();

    protected AbstractInputProcess(S sender, InputProcessListener<I> listener, InputProcessPriority processPriority) {
        this.listener = Objects.requireNonNull(listener);
        this.sender = Objects.requireNonNull(sender);
        this.processPriority = processPriority == null ? InputProcessPriority.NORMAL : processPriority;

        linkInputProcessListener();
    }

    /**
     * Code to run when the input process starts
     * @return
     */
    protected abstract void onStart();

    protected void linkInputProcessListener() {
        this.listener.linkInputProcess(this::acceptInput, this::failProcess);
    }

    protected void acceptInput(I input) {
        // TODO
    }

    protected void failProcess(InputProcessFailureReason reason) {
        if (reason == null)
            reason = InputProcessFailureReason.UNKNOWN;
        // TODO
    }

    /**
     * @return true if the process was able to start
     */
    protected boolean onStartCallback() {
        if (getState() != InputProcessState.QUEUED)
            throw new IllegalStateException("The start callback was invoked but the input process state is not queued");
        state = InputProcessState.RUNNING;
        return false;
    }

    @Override
    public boolean queue(InputProcessManager manager) {
        if (state != InputProcessState.CREATED)
            throw new IllegalStateException("Attempted to queue an input process but its state is " + getState());
        Objects.requireNonNull(manager);
        this.manager = Optional.of(manager);
        if (! manager.queueProcess(this, this::onStartCallback))
            return false;
        this.state = InputProcessState.QUEUED;
        listener.onQueued();
        return true;
    }

    @Override
    public boolean cancel() {
        if (getState() == InputProcessState.QUEUED) {
            if (! getManager().isPresent())
                throw new IllegalStateException("The input process is queued but has no assigned manager");
            if (getManager().get().unqueueProcess(this)) {
                listener.onCancel();
                state = InputProcessState.CREATED;
                return true;
            }
            return false;
        } else if (getState() == InputProcessState.RUNNING) {
            if (! getManager().isPresent())
                throw new IllegalStateException("The input process is running but has no assigned manager");
            failProcess(InputProcessFailureReason.CANCELLED);
            return true;
        }
        return false;
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
    public Optional<InputProcessManager> getManager() {
        return manager;
    }
}
