package me.nemo_64.betterinputs.api.input;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import me.nemo_64.betterinputs.api.input.modifier.AbstractModifier;
import me.nemo_64.betterinputs.api.util.StagedFuture;
import me.nemo_64.betterinputs.api.util.tick.TickTimer;

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
    private final ConcurrentHashMap<Class<? extends AbstractModifier>, AbstractModifier> modifiers = new ConcurrentHashMap<>();

    private BiConsumer<AbstractModifier, Throwable> modifierExceptionHandler;

    InputProvider(final TickTimer timer, final AbstractInput<V> input, final BiConsumer<InputProvider<V>, String> cancelListener) {
        this.acceptor = new TickAcceptor(this, timer);
        this.input = input;
        this.future = input.asFuture();
        this.cancelListener = cancelListener;
        input.provider(this);
    }

    public StagedFuture<V> asFuture() {
        return future;
    }

    public InputProvider<V> withModifier(AbstractModifier modifier) {
        Objects.requireNonNull(modifier, "Modifier can't be null");
        if (future.isComplete() || modifiers.containsKey(modifier.getClass())) {
            return this;
        }
        modifiers.put(modifier.getClass(), modifier);
        return this;
    }

    public <M extends AbstractModifier> Optional<M> getModifier(Class<M> modifierType) {
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

    public InputProvider<V> withModifierExceptionHandler(BiConsumer<AbstractModifier, Throwable> modifierExceptionHandler) {
        this.modifierExceptionHandler = modifierExceptionHandler;
        return this;
    }

    public boolean cancel() {
        return cancel("Manual cancel");
    }

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

    public boolean isCancelled() {
        return input.isCancelled();
    }

    public boolean isAvailable() {
        return future.isComplete();
    }

    public V get() {
        return future.get();
    }

    final void runTick() {
        if (modifiers.isEmpty()) {
            return;
        }
        AbstractModifier[] modifiers = this.modifiers.values().toArray(AbstractModifier[]::new);
        for (int index = 0; index < modifiers.length; index++) {
            AbstractModifier modifier = modifiers[index];
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
        for (AbstractModifier modifier : modifiers) {
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
