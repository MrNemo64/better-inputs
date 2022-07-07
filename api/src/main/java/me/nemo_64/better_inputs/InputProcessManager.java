package me.nemo_64.better_inputs;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class InputProcessManager {

    protected static final Comparator<QueuedProcessInfo> COMPARATOR = Comparator.comparingInt(process ->
            Objects.requireNonNull(process).process.getPriority().priority());

    private final Map<InputProcessSender<?>, Queue<QueuedProcessInfo>> queuedProcesses = new ConcurrentHashMap<>();
    private final Map<InputProcessSender<?>, InputProcess<?, ?>> runningProcesses = new ConcurrentHashMap<>();

    public int estimateQueuedProcessesFor(InputProcessSender<?> sender) {
        Objects.requireNonNull(sender);
        return queuedProcesses.containsKey(sender) ? queuedProcesses.get(sender).size() : 0;
    }

    public boolean isRunningAProcess(InputProcessSender<?> sender) {
        Objects.requireNonNull(sender);
        return runningProcesses.containsKey(sender);
    }

    public boolean unqueueProcess(InputProcess<?, ?> process) {
        Objects.requireNonNull(process);
        Objects.requireNonNull(process.getSender());
        if (! queuedProcesses.containsKey(process.getSender()))
            return false;
        boolean removed = queuedProcesses.get(process.getSender()).remove(process);
        if (queuedProcesses.get(process.getSender()).isEmpty()) // Clean up
            queuedProcesses.remove(process.getSender());
        return removed;
    }

    public boolean queueProcess(InputProcess<?, ?> process, Runnable startCallback) {
        Objects.requireNonNull(process);
        Objects.requireNonNull(process.getSender());
        QueuedProcessInfo info = QueuedProcessInfo.from(process, startCallback);
        if (isRunningAProcess(process.getSender())) {
            Queue<QueuedProcessInfo> queue = queuedProcesses.computeIfAbsent(process.getSender(), this::createQueueFor);
            return queue.add(info);
        }
        return startProcess(info);
    }

    protected boolean startProcess(QueuedProcessInfo info) {
        // TODO
        return false;
    }

    protected Queue<QueuedProcessInfo> createQueueFor(InputProcessSender<?> sender) {
        return new PriorityQueue<>(COMPARATOR);
    }

    protected static class QueuedProcessInfo implements Comparable<QueuedProcessInfo> {
        InputProcess<?, ?> process;
        Runnable startCallback;

        QueuedProcessInfo(InputProcess<?, ?> process, Runnable startCallback) {
            this.process = Objects.requireNonNull(process);
            this.startCallback = Objects.requireNonNull(startCallback);
        }

        public static QueuedProcessInfo from(InputProcess<?, ?> process, Runnable startCallback) {
            return new QueuedProcessInfo(process, startCallback);
        }

        @Override
        public int compareTo(QueuedProcessInfo o) {
            return COMPARATOR.compare(this, o);
        }
    }

}