package me.nemo_64.better_inputs;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class DefaultInputProcessManager implements InputProcessManager {

    private final Map<InputProcessSender<?>, Queue<InputProcess<?, ?>>> queuedProcesses = new ConcurrentHashMap<>();
    private final Map<InputProcessSender<?>, InputProcessRunner<?>> runningProcesses = new ConcurrentHashMap<>();

    private final Function<InputProcessSender<?>, Queue<InputProcess<?, ?>>> queueGenerator;

    public DefaultInputProcessManager(Function<InputProcessSender<?>, Queue<InputProcess<?, ?>>> queueGenerator) {
        this.queueGenerator = Objects.requireNonNull(queueGenerator);
    }

    public DefaultInputProcessManager() {
        this((sender) -> new PriorityQueue<>(Comparator.comparingInt((process) -> process.getPriority().priority())));
    }

    @Override
    public int estimateQueuedProcessesFor(InputProcessSender<?> sender) {
        Objects.requireNonNull(sender);
        return queuedProcesses.containsKey(sender) ? queuedProcesses.get(sender).size() : 0;
    }

    @Override
    public boolean isRunningAProcess(InputProcessSender<?> sender) {
        Objects.requireNonNull(sender);
        return runningProcesses.containsKey(sender);
    }

    @Override
    public boolean unqueueProcess(InputProcess<?, ?> process) {
        Objects.requireNonNull(process);
        Objects.requireNonNull(process.getSender());
        if (! queuedProcesses.containsKey(process))
            return false;
        boolean removed = queuedProcesses.get(process.getSender()).remove(process);
        if (removed)
            process.getRunner().cancelProcess(InputProcessFailureReason.UNQUEUED);
        checkQueue(process.getSender());
        return removed;
    }

    @Override
    public boolean queueProcess(InputProcess<?, ?> process) {
        Objects.requireNonNull(process);
        boolean queued = queuedProcesses.computeIfAbsent(process.getSender(), queueGenerator).add(process);
        checkRunning(process.getSender());
        return queued;
    }

    @Override
    public Optional<InputProcessRunner<?>> getRunningProcessFor(InputProcessSender<?> sender) {
        Objects.requireNonNull(sender);
        return Optional.ofNullable(runningProcesses.get(sender));
    }

    /**
     * Polls the next process to be run by the sender, if there is any
     * @param sender
     * @return
     */
    protected Optional<InputProcess<?, ?>> nextProcessToRun(InputProcessSender<?> sender) {
        Objects.requireNonNull(sender);
        if(!queuedProcesses.containsKey(sender))
            return Optional.empty();
        Optional<InputProcess<?, ?>> op = Optional.of(queuedProcesses.get(sender).poll());
        checkQueue(sender);
        return op;
    }

    protected void checkQueue(InputProcessSender<?> sender) {
        Objects.requireNonNull(sender);
        if (queuedProcesses.get(sender).isEmpty())
            queuedProcesses.remove(sender);
    }

    /**
     * Checks if the given sender is running a process, if not starts the next one
     * @param sender
     */
    protected void checkRunning(InputProcessSender<?> sender) {
        if(isRunningAProcess(sender))
            return;
        Objects.requireNonNull(sender);
        nextProcessToRun(sender).ifPresent(this::startProcess);
    }

    protected void startProcess(InputProcess<?, ?> process) {
        Objects.requireNonNull(process);
        process.getValue().whenComplete((value, error) -> {
            runningProcesses.remove(process.getSender());
            checkRunning(process.getSender()); // start next process
        });
        InputProcessRunner<?> runner = process.getRunner();
        runner.startProcess();
        runningProcesses.put(process.getSender(), runner);
    }
}
