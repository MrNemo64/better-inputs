package me.nemo_64.betterinputs.nms.packet.listener;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import me.lauriichan.laylib.reflection.ClassUtil;
import me.nemo_64.betterinputs.nms.IPlayerAdapter;
import me.nemo_64.betterinputs.nms.packet.AbstractPacketAdapter;

public final class PacketContainer {

    private final IPacketListener instance;
    private final List<PacketExecutor> executors;

    private boolean global = false;
    private final ArrayList<UUID> users = new ArrayList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final boolean acceptCancelled;

    public PacketContainer(IPacketListener instance) {
        this.instance = Objects.requireNonNull(instance);
        ArrayList<PacketExecutor> executors = new ArrayList<>();
        Method[] methods = ClassUtil.getMethods(instance.getClass());
        boolean acceptCancelled = false;
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            PacketHandler handler = ClassUtil.getAnnotation(method, PacketHandler.class);
            if (handler == null) {
                continue;
            }
            boolean receiveCancelled = handler.value();
            PacketExecutor executor = PacketExecutor.analyze(method, receiveCancelled);
            if (executor == null) {
                continue;
            }
            executors.add(executor);
            if (receiveCancelled) {
                acceptCancelled = true;
            }
        }
        if (executors.isEmpty()) {
            throw new IllegalStateException("PacketContainer is not supposed to be empty!");
        }
        this.acceptCancelled = acceptCancelled;
        this.executors = Collections.unmodifiableList(executors);
    }

    public IPacketListener getInstance() {
        return instance;
    }

    public boolean doesAcceptCancelled() {
        return acceptCancelled;
    }

    public PacketContainer setGlobal(boolean global) {
        this.global = global;
        return this;
    }

    public boolean isGlobal() {
        return global;
    }

    final boolean onPacket(IPlayerAdapter player, AbstractPacketAdapter adapter, boolean cancelled) {
        if (!global) {
            lock.readLock().lock();
            try {
                if (!users.contains(player.getUniqueId())) {
                    return false;
                }
            } finally {
                lock.readLock().unlock();
            }
        }
        Class<?> packetType = adapter.getClass();
        for (PacketExecutor executor : executors) {
            if (!executor.getPacketType().isAssignableFrom(packetType) || (cancelled && !executor.doesAllowCancelled())) {
                continue;
            }
            return executor.execute(this, player, adapter);
        }
        return false;
    }

}
