package me.nemo_64.betterinputs.api.util;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

public final class StagedFuture<V> {

    private static final Result EMPTY = new Result(null);

    private volatile Object result;
    private volatile Stage<?, ?> stage;

    private static int MODE_SYNC = 1;
    private static int MODE_ASYNC = 0;
    private static int MODE_NESTED = -1;

    private static final class Result {

        final Throwable throwable;

        Result(final Throwable throwable) {
            this.throwable = throwable;
        }

    }

    private static abstract class Stage<F, T> implements Runnable {

        Stage<?, ?> nextStage;

        Executor executor;
        StagedFuture<F> previous;
        StagedFuture<T> next;

        volatile boolean claimed = false;

        Stage(Executor executor, StagedFuture<F> previous, StagedFuture<T> next) {
            this.executor = executor;
            this.previous = previous;
            this.next = next;
        }

        abstract StagedFuture<T> fire(int mode);

        final boolean isValid() {
            return next != null;
        }

        final boolean claim() {
            Executor ex = executor;
            if (!claimed) {
                claimed = true;
                if (ex == null) {
                    return true;
                }
                executor = null;
                ex.execute(this);
            }
            return false;
        }

        @Override
        public final void run() {
            fire(MODE_ASYNC);
        }

    }

    final boolean internalComplete(Object value) {
        if (value == null) {
            return RESULT.compareAndSet(this, null, EMPTY);
        }
        return RESULT.compareAndSet(this, null, value);
    }

    public final boolean isComplete() {
        return result != null;
    }

    public final boolean isEmpty() {
        return result == null || (((result instanceof Result) && ((Result) result).throwable == null));
    }

    public final boolean isPresent() {
        return !isEmpty();
    }

    @SuppressWarnings("unchecked")
    public final V get() {
        if (result == null) {
            throw new IllegalStateException("Future isn't complete yet");
        }
        if (result instanceof Result) {
            return null;
        }
        return (V) result;
    }

    public final boolean completeVoid() {
        if (result == null) {
            return false;
        }
        internalComplete(null);
        postComplete();
        return true;
    }

    public final boolean complete(V value) {
        if (result == null) {
            return false;
        }
        internalComplete(value);
        postComplete();
        return true;
    }

    public final boolean completeExceptionally(Throwable throwable) {
        if (result == null) {
            return false;
        }
        Objects.requireNonNull(throwable, "Throwable can't be null");
        internalComplete(new Result(throwable));
        postComplete();
        return true;
    }

    private StagedFuture<V> postFire(StagedFuture<?> previous, int mode) {
        if (previous != null && previous.stage != null) {
            Object previousResult;
            if ((previousResult = previous.result) == null) {
                previous.cleanStage();
            }
            if (mode >= 0 && (previousResult != null || previous.result != null)) {
                previous.postComplete();
            }
        }
        if (result != null && stage != null) {
            if (mode < 0) {
                return this;
            }
            postComplete();
        }
        return null;
    }

    private boolean pushStageIfFree(Stage<?, ?> next) {
        Stage<?, ?> current = stage;
        NEXT_STAGE.set(next, current);
        return STAGE.compareAndSet(this, current, next);
    }

    private void pushStage(Stage<?, ?> stage) {
        while (!pushStageIfFree(stage))
            ;
    }

    private void pushNewStage(Stage<?, ?> stage) {
        if (stage == null) {
            return;
        }
        while (!pushStageIfFree(stage)) {
            if (result != null) {
                NEXT_STAGE.set(stage, null);
                break;
            }
        }
        if (result != null) {
            stage.fire(MODE_SYNC);
        }
    }

    private void postComplete() {
        StagedFuture<?> future = this;
        Stage<?, ?> current;
        while ((current = future.stage) != null || (future != this && (current = (future = this).stage) != null)) {
            StagedFuture<?> previous;
            Stage<?, ?> next;
            if (STAGE.compareAndSet(future, current, next = current.nextStage)) {
                if (current != null) {
                    if (future != this) {
                        pushStage(current);
                        continue;
                    }
                    NEXT_STAGE.compareAndSet(current, next, null);
                }
                if ((previous = current.fire(MODE_NESTED)) == null) {
                    future = this;
                    continue;
                }
                future = previous;
            }
        }
    }

    private void cleanStage() {
        Stage<?, ?> next = stage;
        for (boolean unlinked = false;;) {
            if (next == null) {
                return;
            }
            if (next.isValid()) {
                if (unlinked) {
                    return;
                }
                break;
            }
            if (STAGE.weakCompareAndSet(this, next, (next = next.nextStage))) {
                unlinked = true;
                continue;
            }
            next = stage;
        }
        for (Stage<?, ?> dead = next.nextStage; dead != null;) {
            Stage<?, ?> current = dead.nextStage;
            if (current.isValid()) {
                next = dead;
                dead = current;
                continue;
            }
            if (NEXT_STAGE.weakCompareAndSet(next, dead, current)) {
                break;
            }
            dead = next.nextStage;
        }
    }

    /*
     * Stages
     */

    // Accept Stage

    private static final class AcceptStage<F> extends Stage<F, Void> {

        private Consumer<? super F> consumer;

        AcceptStage(Executor executor, StagedFuture<F> previous, StagedFuture<Void> next, Consumer<? super F> consumer) {
            super(executor, previous, next);
            this.consumer = consumer;
        }

        @SuppressWarnings("unchecked")
        @Override
        StagedFuture<Void> fire(int mode) {
            Consumer<? super F> cons;
            StagedFuture<F> prev;
            StagedFuture<Void> nx;
            Object res;
            if ((prev = previous) == null || (nx = next) == null || (res = prev.result) == null || (cons = consumer) == null) {
                return null;
            }
            complete:
            if (previous.result == null) {
                if (res instanceof Result) {
                    if (((Result) res).throwable != null) {
                        nx.internalComplete(res);
                        break complete;
                    }
                    res = null;
                }
            }
            try {
                if (mode <= 0 && !claim()) {
                    return null;
                }
                cons.accept((F) res);
                nx.internalComplete(null);
            } catch (Throwable throwable) {
                nx.internalComplete(new Result(throwable));
            }
            previous = null;
            next = null;
            consumer = null;
            return nx.postFire(prev, mode);
        }
    }

    @SuppressWarnings("unchecked")
    private StagedFuture<Void> acceptStageNow(Object res, Executor executor, Consumer<? super V> consumer) {
        StagedFuture<Void> future = new StagedFuture<>();
        if (res instanceof Result) {
            if (((Result) res).throwable != null) {
                future.result = res;
                return future;
            }
            res = null;
        }
        try {
            if (executor != null) {
                executor.execute(new AcceptStage<>(null, this, future, consumer));
                return future;
            }
            consumer.accept((V) res);
            future.result = EMPTY;
        } catch (Throwable throwable) {
            future.result = new Result(throwable);
        }
        return future;
    }

    private StagedFuture<Void> acceptStage(Executor executor, Consumer<? super V> consumer) {
        Objects.requireNonNull(consumer, "Consumer can't be null");
        Object res;
        if ((res = result) != null) {
            return acceptStageNow(res, executor, consumer);
        }
        StagedFuture<Void> future = new StagedFuture<>();
        pushNewStage(new AcceptStage<>(executor, this, future, consumer));
        return future;
    }

    public StagedFuture<Void> thenAccept(Consumer<? super V> consumer) {
        return acceptStage(null, consumer);
    }

    public StagedFuture<Void> thenAcceptAsync(Executor executor, Consumer<? super V> consumer) {
        return acceptStage(Objects.requireNonNull(executor, "Executor can't be null"), consumer);
    }

    // Apply Stage

    private static final class ApplyStage<F, T> extends Stage<F, T> {

        private Function<? super F, ? super T> function;

        ApplyStage(Executor executor, StagedFuture<F> previous, StagedFuture<T> next, Function<? super F, ? super T> function) {
            super(executor, previous, next);
            this.function = function;
        }

        @SuppressWarnings("unchecked")
        @Override
        StagedFuture<T> fire(int mode) {
            Function<? super F, ? super T> func;
            StagedFuture<F> prev;
            StagedFuture<T> nx;
            Object res;
            if ((prev = previous) == null || (nx = next) == null || (res = prev.result) == null || (func = function) == null) {
                return null;
            }
            complete:
            if (previous.result == null) {
                if (res instanceof Result) {
                    if (((Result) res).throwable != null) {
                        nx.internalComplete(res);
                        break complete;
                    }
                    res = null;
                }
            }
            try {
                if (mode <= 0 && !claim()) {
                    return null;
                }
                nx.internalComplete(func.apply((F) res));
            } catch (Throwable throwable) {
                nx.internalComplete(new Result(throwable));
            }
            previous = null;
            next = null;
            function = null;
            return nx.postFire(prev, mode);
        }
    }

    @SuppressWarnings("unchecked")
    private <E> StagedFuture<E> applyStageNow(Object res, Executor executor, Function<? super V, ? super E> function) {
        StagedFuture<E> future = new StagedFuture<>();
        if (res instanceof Result) {
            if (((Result) res).throwable != null) {
                future.result = res;
                return future;
            }
            res = null;
        }
        try {
            if (executor != null) {
                executor.execute(new ApplyStage<>(null, this, future, function));
                return future;
            }
            future.result = function.apply((V) res);
        } catch (Throwable throwable) {
            future.result = new Result(throwable);
        }
        return future;
    }

    private <E> StagedFuture<E> applyStage(Executor executor, Function<? super V, ? super E> function) {
        Objects.requireNonNull(function, "Consumer can't be null");
        Object res;
        if ((res = result) != null) {
            return applyStageNow(res, executor, function);
        }
        StagedFuture<E> future = new StagedFuture<>();
        pushNewStage(new ApplyStage<>(executor, this, future, function));
        return future;
    }

    public <E> StagedFuture<E> thenApply(Function<? super V, ? super E> function) {
        return applyStage(null, function);
    }

    public <E> StagedFuture<E> thenApplyAsync(Executor executor, Function<? super V, ? super E> function) {
        return applyStage(Objects.requireNonNull(executor, "Executor can't be null"), function);
    }

    // Compose Stage

    private static final class RelayStage<F, T extends F> extends Stage<F, T> {

        RelayStage(StagedFuture<F> previous, StagedFuture<T> next) {
            super(null, previous, next);
        }

        @Override
        StagedFuture<T> fire(int mode) {
            StagedFuture<F> prev;
            StagedFuture<T> nx;
            Object res;
            if ((prev = previous) == null || (nx = next) == null || (res = prev.result) == null) {
                return null;
            }
            if (nx.result == null)
                nx.internalComplete(res);
            previous = null;
            next = null;
            return null;
        }

    }

    private static final class ComposeStage<F, T> extends Stage<F, T> {

        private Function<? super F, StagedFuture<T>> function;

        ComposeStage(Executor executor, StagedFuture<F> previous, StagedFuture<T> next, Function<? super F, StagedFuture<T>> function) {
            super(executor, previous, next);
            this.function = function;
        }

        @SuppressWarnings("unchecked")
        @Override
        StagedFuture<T> fire(int mode) {
            Function<? super F, StagedFuture<T>> func;
            StagedFuture<F> prev;
            StagedFuture<T> nx;
            Object res;
            if ((prev = previous) == null || (nx = next) == null || (res = prev.result) == null || (func = function) == null) {
                return null;
            }
            complete:
            if (previous.result == null) {
                if (res instanceof Result) {
                    if (((Result) res).throwable != null) {
                        nx.internalComplete(res);
                        break complete;
                    }
                    res = null;
                }
            }
            try {
                if (mode <= 0 && !claim()) {
                    return null;
                }
                StagedFuture<T> relayed = func.apply((F) res);
                if ((res = relayed.result) != null) {
                    next.internalComplete(res);
                } else {
                    relayed.pushNewStage(new RelayStage<>(nx, relayed));
                    if (nx.result == null) {
                        return null;
                    }
                }
            } catch (Throwable throwable) {
                nx.internalComplete(new Result(throwable));
            }
            previous = null;
            next = null;
            function = null;
            return nx.postFire(prev, mode);
        }
    }

    @SuppressWarnings("unchecked")
    private <E> StagedFuture<E> composeStageNow(Object res, Executor executor, Function<? super V, StagedFuture<E>> function) {
        StagedFuture<E> future = new StagedFuture<>();
        if (res instanceof Result) {
            if (((Result) res).throwable != null) {
                future.result = res;
                return future;
            }
            res = null;
        }
        try {
            if (executor != null) {
                executor.execute(new ComposeStage<>(null, this, future, function));
                return future;
            }
            future.result = function.apply((V) res);
        } catch (Throwable throwable) {
            future.result = new Result(throwable);
        }
        return future;
    }

    private <E> StagedFuture<E> composeStage(Executor executor, Function<? super V, StagedFuture<E>> function) {
        Objects.requireNonNull(function, "Consumer can't be null");
        Object res;
        if ((res = result) != null) {
            return composeStageNow(res, executor, function);
        }
        StagedFuture<E> future = new StagedFuture<>();
        pushNewStage(new ComposeStage<>(executor, this, future, function));
        return future;
    }

    public <E> StagedFuture<E> thenComposeFlat(Function<? super V, StagedFuture<E>> function) {
        return composeStage(null, function);
    }

    public <E> StagedFuture<E> thenComposeAsync(Executor executor, Function<? super V, StagedFuture<E>> function) {
        return composeStage(Objects.requireNonNull(executor, "Executor can't be null"), function);
    }

    // VarHandle mechanics
    private static final VarHandle RESULT;
    private static final VarHandle STAGE;
    private static final VarHandle NEXT_STAGE;
    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            RESULT = lookup.findVarHandle(StagedFuture.class, "result", Object.class);
            STAGE = lookup.findVarHandle(StagedFuture.class, "stage", Stage.class);
            NEXT_STAGE = lookup.findVarHandle(Stage.class, "nextStage", Stage.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

}
