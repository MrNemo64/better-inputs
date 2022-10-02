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

    /**
     * Gets the set provider
     * 
     * @return the set provider
     */
    public final InputProvider<V> provider() {
        return provider;
    }

    /**
     * Accepts a value to complete the input process
     * 
     * @param value the computed / provided value
     */
    protected final void completeValue(V value) {
        if (provider != null) {
            provider.markComplete();
        }
        future.complete(value);
    }

    /**
     * Accepts a exception to complete the input process
     * 
     * @param throwable the provided exception
     */
    protected final void completeException(Throwable throwable) {
        if (provider != null) {
            provider.markComplete();
        }
        future.completeExceptionally(throwable);
    }

    /**
     * Is called once the input process is started
     * 
     * @param provider the input provider which handles the process
     * @param actor    the actor that this process is assigned to
     */
    protected abstract void onStart(InputProvider<V> provider, IPlatformActor<?> actor);

    /**
     * Is called once the input process is cancelled and can deny the cancellation
     * of it
     * 
     * @return {@code true} if the input process was able to be cancelled otherwise
     *             {@code false}
     */
    protected boolean onCancel() {
        return false;
    }

}
