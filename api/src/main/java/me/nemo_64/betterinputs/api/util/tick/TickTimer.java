package me.nemo_64.betterinputs.api.util.tick;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;

public final class TickTimer {

    private static final long NANO_TIME = TimeUnit.SECONDS.toNanos(1);

    private static final long TICK_MILLIS = 50L;
    private static final long TICK_NANOS = TimeUnit.MILLISECONDS.toNanos(TICK_MILLIS);

    private final Thread timerThread;

    private volatile boolean paused = false;
    private volatile int tps = 0;

    private volatile boolean shutdown = false;

    private final ArrayList<Runnable> actions = new ArrayList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private BiConsumer<TickTimer, Throwable> exceptionHandler;

    public TickTimer(String name) {
        this.timerThread = new Thread(this::execute, name);
        timerThread.setDaemon(true);
        timerThread.start();
    }

    public String getName() {
        return timerThread.getName();
    }

    public void shutdown() {
        if (shutdown) {
            return;
        }
        shutdown = true;
    }

    public void pause() {
        if (paused) {
            return;
        }
        paused = true;
    }

    public void start() {
        if (!paused || shutdown) {
            return;
        }
        paused = false;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isAlive() {
        return !shutdown;
    }

    public int getTps() {
        return tps;
    }

    public void setExceptionHandler(BiConsumer<TickTimer, Throwable> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public TickTimer addAction(Runnable action) {
        if (action == null) {
            return this;
        }
        lock.readLock().lock();
        try {
            if (actions.contains(action)) {
                return this;
            }
        } finally {
            lock.readLock().unlock();
        }
        lock.writeLock().lock();
        try {
            actions.add(action);
        } finally {
            lock.writeLock().unlock();
        }
        return this;
    }

    public TickTimer removeAction(Runnable action) {
        if (action == null) {
            return this;
        }
        lock.readLock().lock();
        try {
            if (!actions.contains(action)) {
                return this;
            }
        } finally {
            lock.readLock().unlock();
        }
        lock.writeLock().lock();
        try {
            actions.remove(action);
        } finally {
            lock.writeLock().unlock();
        }
        return this;
    }

    private void runTick() throws InterruptedException {
        lock.readLock().lock();
        Runnable[] actions;
        try {
            if (this.actions.isEmpty()) {
                return;
            }
            actions = this.actions.toArray(Runnable[]::new);
        } finally {
            lock.readLock().unlock();
        }
        for (Runnable action : actions) {
            try {
                action.run();
            } catch (Throwable throwable) {
                if (throwable instanceof InterruptedException) {
                    throw (InterruptedException) throwable;
                }
                exceptionHandler.accept(this, throwable);
            }
        }
    }

    private void execute() {
        long nextLength = TICK_NANOS;
        long tickMillis;
        int tickNanos;
        long cycles;
        long prevNanoTime = System.nanoTime();
        long nanoTime = prevNanoTime;
        long delta = 0;
        long elapsed = 0;
        int counter = 0;
        while (true) {
            if (shutdown) {
                break;
            }
            if (paused) {
                nextLength = TICK_NANOS;
                try {
                    Thread.sleep(TICK_MILLIS);
                } catch (InterruptedException e) {
                    continue;
                }
            }
            try {
                while (!paused) {
                    runTick();
                    prevNanoTime = nanoTime;
                    nanoTime = System.nanoTime();
                    delta = nanoTime - prevNanoTime;
                    elapsed += delta;
                    if (elapsed >= NANO_TIME) {
                        elapsed = NANO_TIME - elapsed;
                        this.tps = counter;
                        counter = 0;
                    }
                    tickMillis = TimeUnit.NANOSECONDS.toMillis(nextLength - delta);
                    tickNanos = (int) (nextLength - TimeUnit.MILLISECONDS.toNanos(tickMillis));
                    if (tickMillis > 2) {
                        Thread.sleep(tickMillis, tickNanos);
                        continue;
                    }
                    cycles = (TimeUnit.MILLISECONDS.toNanos(tickMillis) + tickNanos) / 2;
                    while (cycles-- >= 0) {
                        Thread.yield();
                    }
                }
            } catch (InterruptedException interrupt) {
                continue;
            }
        }
    }

}
