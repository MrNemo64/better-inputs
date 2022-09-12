package me.nemo_64.betterinputs.api.input;

public abstract class AbstractInput<V> {

    private final StagedFuture<V> future = new StagedFuture<>();
    
    protected final void completeValue(V value) {
        future.complete(value);
    }

    protected final void completeException(Throwable throwable) {
        future.completeExceptionally(throwable);
    }

}
