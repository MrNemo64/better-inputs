package me.nemo_64.betterinputs.bukkit.nms.packet.listener;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;
import me.nemo_64.betterinputs.api.util.argument.NotEnoughArgumentsException;
import me.nemo_64.betterinputs.bukkit.nms.PlayerAdapter;
import me.nemo_64.betterinputs.bukkit.nms.VersionHandler;
import me.nemo_64.betterinputs.bukkit.nms.packet.AbstractPacket;
import me.nemo_64.betterinputs.bukkit.nms.packet.AbstractPacketOut;

public abstract class PacketManager {

    private final ArrayList<PacketContainer> listeners = new ArrayList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    private final ExecutorService mainService;
    private final ExecutorService asyncService;
    
    public PacketManager(final VersionHandler handler) {
        this.mainService = handler.mainService();
        this.asyncService = handler.asyncService();
    }

    public final PacketContainer register(IPacketListener listener) {
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
        PacketContainer container = new PacketContainer(mainService, asyncService, listener);
        lock.writeLock().lock();
        try {
            listeners.add(container);
        } finally {
            lock.writeLock().unlock();
        }
        return container;
    }

    public final boolean unregister(PacketContainer container) {
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

    public final boolean call(PlayerAdapter player, AbstractPacket packet) {
        lock.readLock().lock();
        try {
            if (listeners.isEmpty()) {
                return false;
            }
        } finally {
            lock.readLock().unlock();
        }
        boolean cancelled = false;
        PacketContainer current;
        for (int index = 0;; index++) {
            lock.readLock().lock();
            try {
                if (index >= listeners.size()) {
                    return cancelled;
                }
                current = listeners.get(index);
            } finally {
                lock.readLock().unlock();
            }
            if (cancelled && !current.doesAcceptCancelled()) {
                continue;
            }
            if (current.onPacket(player, packet, cancelled)) {
                cancelled = true;
            }
        }
    }

    public abstract <P extends AbstractPacketOut> P createPacket(ArgumentMap map, Class<P> packetType)
        throws NotEnoughArgumentsException, IllegalStateException, IllegalArgumentException;

}
