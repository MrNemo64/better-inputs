package me.nemo_64.betterinputs.api.input;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import me.lauriichan.laylib.reflection.StackTracker;
import me.nemo_64.betterinputs.api.BetterInputs;
import me.nemo_64.betterinputs.api.input.modifier.AbstractModifier;
import me.nemo_64.betterinputs.api.platform.IPlatformActor;
import me.nemo_64.betterinputs.api.util.StagedFuture;
import me.nemo_64.betterinputs.api.util.tick.TickTimer;

@SuppressWarnings({
    "rawtypes",
    "unchecked"
})
public final class InputProvider<V> {

    private static final class TickAcceptor implements Runnable {

        private final InputProvider<?> provider;
        private final TickTimer timer;

        public TickAcceptor(final InputProvider<?> provider, final TickTimer timer) {
            this.provider = provider;
            this.timer = timer.addAction(this);
        }

        @Override
        public void run() {
            provider.runTick();
        }

        public void remove() {
            timer.removeAction(this);
        }

    }

    private final TickAcceptor acceptor;

    private final AbstractInput<V> input;
    private final StagedFuture<V> future;

    private final BiConsumer<InputProvider<V>, String> cancelListener;
    private final ConcurrentHashMap<Class<? extends AbstractModifier>, AbstractModifier<V>> modifiers = new ConcurrentHashMap<>();

    private final IPlatformActor<?> actor;

    private BiConsumer<AbstractModifier<V>, Throwable> modifierExceptionHandler;

    public InputProvider(final IPlatformActor<?> actor, final TickTimer timer, final AbstractInput<V> input,
        final Consumer<Throwable> exceptionHandler, final BiConsumer<InputProvider<V>, String> cancelListener) {
        if (StackTracker.getCallerClass().filter(clazz -> BetterInputs.class.isAssignableFrom(clazz)).isEmpty()) {
            throw new UnsupportedOperationException("Can only be created by BetterInputs");
        }
        this.actor = actor;
        this.input = input;
        this.future = input.asFuture().withExceptionHandler(exceptionHandler);
        this.acceptor = new TickAcceptor(this, timer);
        this.cancelListener = cancelListener;
    }

    /**
     * Gets the future of this process and starts the input process on first call
     * 
     * @return the future
     */
    public StagedFuture<V> asFuture() {
        if (input.provider() == null) {
            input.provider(this);
        }
        return future;
    }

    /**
     * Adds a input modifier
     * 
     * @param  modifier the modifier to be added
     * 
     * @return          the same input provider
     */
    public InputProvider<V> withModifier(AbstractModifier<V> modifier) {
        Objects.requireNonNull(modifier, "Modifier can't be null");
        if (future.isComplete() || modifiers.containsKey(modifier.getClass())) {
            return this;
        }
        modifiers.put(modifier.getClass(), modifier);
        return this;
    }

    /**
     * Gets an input modifier
     * 
     * @param  <M>          the provided modifier type
     * @param  modifierType the input modifier type
     * 
     * @return              an {@code java.lang.Optional} containing the input
     *                          modifier or {@code null}
     */
    public <M extends AbstractModifier<V>> Optional<M> getModifier(Class<M> modifierType) {
        if (modifierType == null) {
            return Optional.empty();
        }
        for (Class<?> clazz : modifiers.keySet()) {
            if (modifierType.isAssignableFrom(clazz)) {
                return Optional.ofNullable(modifiers.get(modifierType)).map(modifierType::cast);
            }
        }
        return Optional.empty();
    }

    /**
     * Checks if a modifier is set
     * 
     * @param  modifierType the modifier type to be checked
     * 
     * @return              {@code true} if the modifier type is set otherwise
     *                          {@code false}
     */
    public boolean hasModifier(Class<? extends AbstractModifier<V>> modifierType) {
        if (modifierType == null) {
            return false;
        }
        for (Class<?> clazz : modifiers.keySet()) {
            if (modifierType.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the modifier exception handler
     * 
     * @param  modifierExceptionHandler the handler to be set
     * 
     * @return                          the same input provider
     */
    public InputProvider<V> withModifierExceptionHandler(BiConsumer<AbstractModifier<V>, Throwable> modifierExceptionHandler) {
        this.modifierExceptionHandler = modifierExceptionHandler;
        return this;
    }

    /**
     * Tries to cancel the input process
     * 
     * @return {@code true} if the process was cancelled otherwise {@code false}
     */
    public boolean cancel() {
        return cancel("Manual cancel");
    }

    /**
     * Tries to cancel the input process with a specified reason
     * 
     * @param  reason the reason why the process was cancelled
     * 
     * @return        {@code true} if the process was cancelled otherwise
     *                    {@code false}
     */
    public boolean cancel(String reason) {
        if (input.isCancelled()) {
            return true;
        }
        if (input.cancel()) {
            if (cancelListener != null) {
                cancelListener.accept(this, reason);
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if the input process is cancelled
     * 
     * @return {@code true} if process is cancelled otherwise {@code false}
     */
    public boolean isCancelled() {
        return input.isCancelled();
    }

    /**
     * Checks if the input is available already
     * 
     * @return {@code true} if the input is available otherwise {@code false}
     */
    public boolean isAvailable() {
        return future.isComplete();
    }

    /**
     * Gets the wrapped owning actor
     * 
     * @return the actor
     */
    public IPlatformActor<?> getActor() {
        return actor;
    }

    /**
     * Gets the input of this input process
     * 
     * @return                       the input provided by the process
     * 
     * @throws IllegalStateException if the process isn't done yet
     */
    public V get() throws IllegalStateException {
        return future.get();
    }

    final void runTick() {
        if (modifiers.isEmpty()) {
            return;
        }
        AbstractModifier[] modifiers = this.modifiers.values().toArray(AbstractModifier[]::new);
        for (int index = 0; index < modifiers.length; index++) {
            AbstractModifier<V> modifier = modifiers[index];
            try {
                modifier.tick();
                if (modifier.isExpired()) {
                    modifier.onExpire(this);
                    this.modifiers.remove(modifier.getClass());
                }
            } catch (Throwable throwable) {
                if (modifierExceptionHandler != null) {
                    modifierExceptionHandler.accept(modifier, throwable);
                }
            }
        }
    }

    final void markComplete() {
        acceptor.remove();
        if (input.isCancelled()) {
            return;
        }
        AbstractModifier[] modifiers = this.modifiers.values().toArray(AbstractModifier[]::new);
        this.modifiers.clear();
        for (AbstractModifier<V> modifier : modifiers) {
            try {
                modifier.onDone(this);
            } catch (Throwable throwable) {
                if (modifierExceptionHandler != null) {
                    modifierExceptionHandler.accept(modifier, throwable);
                }
            }
        }
    }

}
