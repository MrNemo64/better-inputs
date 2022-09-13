package me.nemo_64.betterinputs.nms.packet.listener;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import me.nemo_64.betterinputs.nms.packet.AbstractPacketAdapter;

public abstract class AbstractPacketManager {

    private final ArrayList<PacketContainer> listeners = new ArrayList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public PacketContainer register(IPacketListener listener) {
        lock.readLock().lock();
        try {
            for (int index = 0; index < listeners.size(); index++) {
                PacketContainer container = listeners.get(index);
                if (container.getInstance() == listener) {
                    return container;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        PacketContainer container = new PacketContainer(listener);
        lock.writeLock().lock();
        try {
            listeners.add(container);
        } finally {
            lock.writeLock().unlock();
        }
        return container;
    }

    public boolean unregister(PacketContainer container) {
        lock.readLock().lock();
        try {
            if (!listeners.contains(container)) {
                return false;
            }
        } finally {
            lock.readLock().unlock();
        }
        lock.writeLock().lock();
        try {
            return listeners.remove(container);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public boolean call(UUID playerId, AbstractPacketAdapter packet) {
        return false;
    }

}
