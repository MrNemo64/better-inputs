package me.nemo_64.betterinputs.api.input;

import me.nemo_64.betterinputs.api.future.StagedFuture;

public abstract class AbstractInput<V> {

    private final StagedFuture<V> future = new StagedFuture<>();
    
    protected final void completeValue(V value) {
        future.complete(value);
    }

    protected final void completeException(Throwable throwable) {
        future.completeExceptionally(throwable);
    }

}
