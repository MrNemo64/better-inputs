package me.nemo_64.betterinputs.api.input;

import java.util.function.BiConsumer;

import me.nemo_64.betterinputs.api.util.StagedFuture;

public final class InputProvider<V> {

    private final AbstractInput<V> input;
    private final StagedFuture<V> future;

    private final BiConsumer<InputProvider<V>, CancelReason> cancelListener;

    public InputProvider(final AbstractInput<V> input, final BiConsumer<InputProvider<V>, CancelReason> cancelListener) {
        this.input = input;
        this.future = input.asFuture();
        this.cancelListener = cancelListener;
    }
    
    public StagedFuture<V> asFuture() {
        return future;
    }

    public boolean cancel() {
        if (input.isCancelled()) {
            return true;
        }
        if (input.cancel()) {
            if (cancelListener != null) {
                cancelListener.accept(this, CancelReason.MANUAL);
            }
            return true;
        }
        return false;
    }

    public boolean isCancelled() {
        return input.isCancelled();
    }

    public boolean isAvailable() {
        return future.isComplete();
    }

    public V get() {
        return future.get();
    }

}
