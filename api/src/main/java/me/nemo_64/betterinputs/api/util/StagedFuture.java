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

	private Consumer<Throwable> fallbackExceptionHandler;

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

		Consumer<Throwable> exceptionHandler;

		Stage<?, ?> nextStage;

		Executor executor;
		StagedFuture<F> previous;
		StagedFuture<T> next;

		volatile boolean claimed = false;

		Stage(Executor executor, Consumer<Throwable> exceptionHandler, StagedFuture<F> previous, StagedFuture<T> next) {
			this.exceptionHandler = exceptionHandler;
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

	public StagedFuture() {
	}

	private StagedFuture(Consumer<Throwable> fallbackExceptionHandler) {
		this.fallbackExceptionHandler = fallbackExceptionHandler;
	}

	final boolean internalComplete(Object value) {
		if (value == null) {
			return RESULT.compareAndSet(this, null, EMPTY);
		}
		return RESULT.compareAndSet(this, null, value);
	}

	private final <T> StagedFuture<T> newFuture() {
		return new StagedFuture<>(fallbackExceptionHandler);
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

	@SuppressWarnings("unchecked")
	public final V join() throws InterruptedException {
		if (result != null) {
			if (result instanceof Result) {
				return null;
			}
			return (V) result;
		}
		while (result == null) {
			Thread.sleep(100);
		}
		Object result = RESULT.getVolatile(this);
		if (result instanceof Result) {
			return null;
		}
		return (V) result;
	}

	public final boolean completeVoid() {
		return complete(null);
	}

	public final boolean complete(V value) {
		if (result != null) {
			return false;
		}
		internalComplete(value);
		postComplete();
		return true;
	}

	public final boolean completeExceptionally(Throwable throwable) {
		if (result != null) {
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
		// This is required to prevent a compiler error at NEXT_STAGE#compareAndSet
		// The reason for the compiler error seems to be the signature of the method
		// used
		// More info here:
		// https://stackoverflow.com/questions/52962939/java-lang-nosuchmethoderror-varhandle-compareandsetvariablehandlesexample-stat
		@SuppressWarnings("unused")
		boolean _ignore;
		while ((current = future.stage) != null || (future != this && (current = (future = this).stage) != null)) {
			StagedFuture<?> previous;
			Stage<?, ?> next;
			if (STAGE.compareAndSet(future, current, next = current.nextStage)) {
				if (current != null) {
					if (future != this) {
						pushStage(current);
						continue;
					}
					_ignore = NEXT_STAGE.compareAndSet(current, next, null); // (v1)
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
	 * Exceptions
	 */

	private final Consumer<Throwable> findFallbackExceptionHandler() {
		if (fallbackExceptionHandler != null) {
			return fallbackExceptionHandler;
		}
		StagedFuture<?> next = this;
		while (next.stage != null && (next = next.stage.next) != null) {
			if (next.fallbackExceptionHandler != null) {
				return next.fallbackExceptionHandler;
			}
		}
		return null;
	}

	public StagedFuture<V> withExceptionHandler(Consumer<Throwable> exceptionHandler) {
		this.fallbackExceptionHandler = exceptionHandler;
		return this;
	}

	/*
	 * Stages
	 */

	// Accept Stage

	private static final class AcceptStage<F> extends Stage<F, Void> {

		private Consumer<? super F> consumer;

		AcceptStage(Executor executor, Consumer<Throwable> exceptionHandler, StagedFuture<F> previous,
				StagedFuture<Void> next, Consumer<? super F> consumer) {
			super(executor, exceptionHandler, previous, next);
			this.consumer = consumer;
		}

		@SuppressWarnings("unchecked")
		@Override
		StagedFuture<Void> fire(int mode) {
			Consumer<Throwable> expHndl = exceptionHandler;
			Consumer<? super F> cons;
			StagedFuture<F> prev;
			StagedFuture<Void> nx;
			Object res;
			if ((prev = previous) == null || (nx = next) == null || (res = prev.result) == null
					|| (cons = consumer) == null) {
				return null;
			}
			if (res != null) {
				if (res instanceof Result) {
					Throwable thr;
					complete: if ((thr = ((Result) res).throwable) != null) {
						try {
							if (expHndl != null) {
								expHndl.accept(thr);
								nx.internalComplete(EMPTY);
								break complete;
							}
						} catch (Throwable throwable) {
							res = new Result(throwable);
						}
						nx.internalComplete(res);
						break complete;
					}
					res = null;
				}
			}
			execute: try {
				if (mode <= 0 && !claim()) {
					return null;
				}
				cons.accept((F) res);
				nx.internalComplete(null);
			} catch (Throwable throwable) {
				try {
					if (expHndl != null) {
						expHndl.accept(throwable);
						nx.internalComplete(EMPTY);
						break execute;
					}
				} catch (Throwable thr) {
					// Replace exception as the exception handler was not able to execute which is
					// more important than
					// the previous exception
					throwable = thr;
				}
				nx.internalComplete(new Result(throwable));
			}
			previous = null;
			next = null;
			consumer = null;
			exceptionHandler = null;
			return nx.postFire(prev, mode);
		}
	}

	@SuppressWarnings("unchecked")
	private StagedFuture<Void> acceptStageNow(Object res, Executor executor, Consumer<Throwable> exceptionHandler,
			Consumer<? super V> consumer) {
		StagedFuture<Void> future = newFuture();
		if (res instanceof Result) {
			Throwable throwable;
			if ((throwable = ((Result) res).throwable) != null) {
				future.result = res;
				if (exceptionHandler != null) {
					exceptionHandler.accept(throwable);
				}
				return future;
			}
			res = null;
		}
		try {
			if (executor != null) {
				executor.execute(new AcceptStage<>(null, exceptionHandler, this, future, consumer));
				return future;
			}
			consumer.accept((V) res);
			future.result = EMPTY;
		} catch (Throwable throwable) {
			future.result = new Result(throwable);
		}
		return future;
	}

	private StagedFuture<Void> acceptStage(Executor executor, Consumer<Throwable> exceptionHandler,
			Consumer<? super V> consumer) {
		Objects.requireNonNull(consumer, "Consumer can't be null");
		Object res;
		if ((res = result) != null) {
			return acceptStageNow(res, executor, exceptionHandler, consumer);
		}
		StagedFuture<Void> future = newFuture();
		pushNewStage(new AcceptStage<>(executor, exceptionHandler, this, future, consumer));
		return future;
	}

	public StagedFuture<Void> thenAccept(Consumer<? super V> consumer) {
		return acceptStage(null, findFallbackExceptionHandler(), consumer);
	}

	public StagedFuture<Void> thenAcceptAsync(Executor executor, Consumer<? super V> consumer) {
		return acceptStage(Objects.requireNonNull(executor, "Executor can't be null"), findFallbackExceptionHandler(),
				consumer);
	}

	public StagedFuture<Void> thenAcceptExceptionally(Consumer<? super V> consumer,
			Consumer<Throwable> exceptionHandler) {
		return acceptStage(null, exceptionHandler, consumer);
	}

	public StagedFuture<Void> thenAcceptExceptionallyAsync(Executor executor, Consumer<? super V> consumer,
			Consumer<Throwable> exceptionHandler) {
		return acceptStage(Objects.requireNonNull(executor, "Executor can't be null"), exceptionHandler, consumer);
	}

	// Apply Stage

	private static final class ApplyStage<F, T> extends Stage<F, T> {

		private Function<? super F, ? super T> function;

		ApplyStage(Executor executor, Consumer<Throwable> exceptionHandler, StagedFuture<F> previous,
				StagedFuture<T> next, Function<? super F, ? super T> function) {
			super(executor, exceptionHandler, previous, next);
			this.function = function;
		}

		@SuppressWarnings("unchecked")
		@Override
		StagedFuture<T> fire(int mode) {
			Consumer<Throwable> expHndl = exceptionHandler;
			Function<? super F, ? super T> func;
			StagedFuture<F> prev;
			StagedFuture<T> nx;
			Object res;
			if ((prev = previous) == null || (nx = next) == null || (res = prev.result) == null
					|| (func = function) == null) {
				return null;
			}
			if (res != null) {
				if (res instanceof Result) {
					Throwable thr;
					complete: if ((thr = ((Result) res).throwable) != null) {
						try {
							if (expHndl != null) {
								expHndl.accept(thr);
								nx.internalComplete(EMPTY);
								break complete;
							}
						} catch (Throwable throwable) {
							res = new Result(throwable);
						}
						nx.internalComplete(res);
						break complete;
					}
					res = null;
				}
			}
			execute: try {
				if (mode <= 0 && !claim()) {
					return null;
				}
				nx.internalComplete(func.apply((F) res));
			} catch (Throwable throwable) {
				try {
					if (expHndl != null) {
						expHndl.accept(throwable);
						nx.internalComplete(EMPTY);
						break execute;
					}
				} catch (Throwable thr) {
					// Replace exception as the exception handler was not able to execute which is
					// more important than
					// the previous exception
					throwable = thr;
				}
				nx.internalComplete(new Result(throwable));
			}
			previous = null;
			next = null;
			function = null;
			exceptionHandler = null;
			return nx.postFire(prev, mode);
		}
	}

	@SuppressWarnings("unchecked")
	private <E> StagedFuture<E> applyStageNow(Object res, Executor executor, Consumer<Throwable> exceptionHandler,
			Function<? super V, ? super E> function) {
		StagedFuture<E> future = newFuture();
		if (res instanceof Result) {
			if (((Result) res).throwable != null) {
				future.result = res;
				return future;
			}
			res = null;
		}
		try {
			if (executor != null) {
				executor.execute(new ApplyStage<>(null, exceptionHandler, this, future, function));
				return future;
			}
			future.result = function.apply((V) res);
		} catch (Throwable throwable) {
			future.result = new Result(throwable);
		}
		return future;
	}

	private <E> StagedFuture<E> applyStage(Executor executor, Consumer<Throwable> exceptionHandler,
			Function<? super V, ? super E> function) {
		Objects.requireNonNull(function, "Consumer can't be null");
		Object res;
		if ((res = result) != null) {
			return applyStageNow(res, executor, exceptionHandler, function);
		}
		StagedFuture<E> future = newFuture();
		pushNewStage(new ApplyStage<>(executor, exceptionHandler, this, future, function));
		return future;
	}

	public <E> StagedFuture<E> thenApply(Function<? super V, ? super E> function) {
		return applyStage(null, findFallbackExceptionHandler(), function);
	}

	public <E> StagedFuture<E> thenApplyAsync(Executor executor, Function<? super V, ? super E> function) {
		return applyStage(Objects.requireNonNull(executor, "Executor can't be null"), findFallbackExceptionHandler(),
				function);
	}

	public <E> StagedFuture<E> thenApplyExceptionally(Function<? super V, ? super E> function,
			Consumer<Throwable> exceptionHandler) {
		return applyStage(null, exceptionHandler, function);
	}

	public <E> StagedFuture<E> thenApplyExceptionallyAsync(Executor executor, Function<? super V, ? super E> function,
			Consumer<Throwable> exceptionHandler) {
		return applyStage(Objects.requireNonNull(executor, "Executor can't be null"), exceptionHandler, function);
	}

	// Compose Stage

	private static final class RelayStage<F, T extends F> extends Stage<F, T> {

		RelayStage(StagedFuture<F> previous, StagedFuture<T> next) {
			super(null, null, previous, next);
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

		ComposeStage(Executor executor, Consumer<Throwable> exceptionHandler, StagedFuture<F> previous,
				StagedFuture<T> next, Function<? super F, StagedFuture<T>> function) {
			super(executor, exceptionHandler, previous, next);
			this.function = function;
		}

		@SuppressWarnings("unchecked")
		@Override
		StagedFuture<T> fire(int mode) {
			Consumer<Throwable> expHndl = exceptionHandler;
			Function<? super F, StagedFuture<T>> func;
			StagedFuture<F> prev;
			StagedFuture<T> nx;
			Object res;
			if ((prev = previous) == null || (nx = next) == null || (res = prev.result) == null
					|| (func = function) == null) {
				return null;
			}
			if (previous.result == null) {
				if (res instanceof Result) {
					Throwable thr;
					complete: if ((thr = ((Result) res).throwable) != null) {
						try {
							if (expHndl != null) {
								expHndl.accept(thr);
								nx.internalComplete(EMPTY);
								break complete;
							}
						} catch (Throwable throwable) {
							res = new Result(throwable);
						}
						nx.internalComplete(res);
						break complete;
					}
					res = null;
				}
			}
			execute: try {
				if (mode <= 0 && !claim()) {
					return null;
				}
				StagedFuture<T> relayed = func.apply((F) res);
				if ((res = relayed.result) != null) {
					nx.internalComplete(res);
				} else {
					relayed.pushNewStage(new RelayStage<>(nx, relayed));
					if (nx.result == null) {
						return null;
					}
				}
			} catch (Throwable throwable) {
				try {
					if (expHndl != null) {
						expHndl.accept(throwable);
						nx.internalComplete(EMPTY);
						break execute;
					}
				} catch (Throwable thr) {
					// Replace exception as the exception handler was not able to execute which is
					// more important than
					// the previous exception
					throwable = thr;
				}
				nx.internalComplete(new Result(throwable));
			}
			previous = null;
			next = null;
			function = null;
			exceptionHandler = null;
			return nx.postFire(prev, mode);
		}
	}

	@SuppressWarnings("unchecked")
	private <E> StagedFuture<E> composeStageNow(Object res, Executor executor, Consumer<Throwable> exceptionHandler,
			Function<? super V, StagedFuture<E>> function) {
		StagedFuture<E> future = newFuture();
		if (res instanceof Result) {
			if (((Result) res).throwable != null) {
				future.result = res;
				return future;
			}
			res = null;
		}
		try {
			if (executor != null) {
				executor.execute(new ComposeStage<>(null, exceptionHandler, this, future, function));
				return future;
			}
			future.result = function.apply((V) res);
		} catch (Throwable throwable) {
			future.result = new Result(throwable);
		}
		return future;
	}

	private <E> StagedFuture<E> composeStage(Executor executor, Consumer<Throwable> exceptionHandler,
			Function<? super V, StagedFuture<E>> function) {
		Objects.requireNonNull(function, "Consumer can't be null");
		Object res;
		if ((res = result) != null) {
			return composeStageNow(res, executor, exceptionHandler, function);
		}
		StagedFuture<E> future = newFuture();
		pushNewStage(new ComposeStage<>(executor, exceptionHandler, this, future, function));
		return future;
	}

	public <E> StagedFuture<E> thenCompose(Function<? super V, StagedFuture<E>> function) {
		return composeStage(null, findFallbackExceptionHandler(), function);
	}

	public <E> StagedFuture<E> thenComposeAsync(Executor executor, Function<? super V, StagedFuture<E>> function) {
		return composeStage(Objects.requireNonNull(executor, "Executor can't be null"), findFallbackExceptionHandler(),
				function);
	}

	public <E> StagedFuture<E> thenComposeExceptionally(Function<? super V, StagedFuture<E>> function,
			Consumer<Throwable> exceptionHandler) {
		return composeStage(null, exceptionHandler, function);
	}

	public <E> StagedFuture<E> thenComposeExceptionallyAsync(Executor executor,
			Function<? super V, StagedFuture<E>> function, Consumer<Throwable> exceptionHandler) {
		return composeStage(Objects.requireNonNull(executor, "Executor can't be null"), exceptionHandler, function);
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
