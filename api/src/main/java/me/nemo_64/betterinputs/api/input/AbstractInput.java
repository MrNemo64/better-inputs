package me.nemo_64.betterinputs.api.input;

import me.nemo_64.betterinputs.api.platform.IPlatformActor;
import me.nemo_64.betterinputs.api.util.StagedFuture;

public abstract class AbstractInput<V> {
    
    private final StagedFuture<V> future = new StagedFuture<>();
    private InputProvider<V> provider;
    private boolean cancelled = false;

    final void provider(InputProvider<V> provider) {
        if (this.provider != null) {
            return;
        }
        this.provider = provider;
        if (future.isComplete()) {
            provider.markComplete();
            return;
        }
        onStart(provider, provider.getActor());
    }

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

    protected final InputProvider<V> provider() {
        return provider;
    }

    protected final void completeValue(V value) {
        if (provider != null) {
            provider.markComplete();
        }
        future.complete(value);
    }

    protected final void completeException(Throwable throwable) {
        if (provider != null) {
            provider.markComplete();
        }
        future.completeExceptionally(throwable);
    }
    
    protected abstract void onStart(InputProvider<V> provider, IPlatformActor<?> actor);

    protected boolean onCancel() {
        return false;
    }

}
