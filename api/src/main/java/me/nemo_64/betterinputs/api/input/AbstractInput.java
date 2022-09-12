package me.nemo_64.betterinputs.api.input;

import me.nemo_64.betterinputs.api.util.StagedFuture;

public abstract class AbstractInput<V> {

    private final StagedFuture<V> future = new StagedFuture<>();
    private boolean cancelled = false;

    final StagedFuture<V> asFuture() {
        return future;
    }

    final boolean cancel() {
        if (!cancelled) {
            if (onCancel()) {
                return cancelled = true;
            }
        }
        return false;
    }

    final boolean isCancelled() {
        return cancelled;
    }

    protected final void completeValue(V value) {
        future.complete(value);
    }

    protected final void completeException(Throwable throwable) {
        future.completeExceptionally(throwable);
    }

    protected boolean onCancel() {
        return false;
    }

}
