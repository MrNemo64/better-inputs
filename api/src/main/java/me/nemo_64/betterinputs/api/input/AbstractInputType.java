package me.nemo_64.betterinputs.api.input;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractInputType<V> implements IInputType<V> {

    private final SimpleFuture<V> future = new SimpleFuture<>(this);
    private InputResult<V> result = null;

    public final void complete(V value) {
        if (result != null) {
            return;
        }
        this.result = InputResult.of(value);
        onComplete(result);
    }

    public final void complete(Throwable throwable) {
        if (result != null) {
            return;
        }
        this.result = InputResult.of(throwable);
        onComplete(result);
    }

    protected void onComplete(InputResult<V> result) {}

    protected void onCancel() {}

    @Override
    public final Future<InputResult<V>> future() {
        return future;
    }

    private static final class SimpleFuture<T> implements Future<InputResult<T>> {

        private final AbstractInputType<T> type;
        private boolean cancelled = false;

        public SimpleFuture(final AbstractInputType<T> type) {
            this.type = type;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            if (!mayInterruptIfRunning || cancelled || type.result != null) {
                return false;
            }
            cancelled = true;
            type.onCancel();
            return true;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public boolean isDone() {
            return type.result != null;
        }

        @Override
        public InputResult<T> get() throws InterruptedException, ExecutionException {
            if (cancelled || type.result != null) {
                return type.result;
            }
            while (!cancelled && type.result == null) {
                Thread.sleep(100);
            }
            return type.result;
        }

        @Override
        public InputResult<T> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            if (cancelled || type.result != null) {
                return type.result;
            }
            long ms = unit.toMillis(timeout);
            while (ms > 0) {
                long sleep = Math.min(ms, 100);
                Thread.sleep(sleep);
                ms -= sleep;
                if (cancelled || type.result != null) {
                    return type.result;
                }
            }
            if (ms <= 0 && type.result == null) {
                throw new TimeoutException();
            }
            return type.result;
        }

    }

}
