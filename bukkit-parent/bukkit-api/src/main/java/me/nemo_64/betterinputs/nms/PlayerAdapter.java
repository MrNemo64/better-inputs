package me.nemo_64.betterinputs.nms;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import me.nemo_64.betterinputs.nms.packet.AbstractPacketOut;

public abstract class PlayerAdapter {

    protected final ConcurrentHashMap<String, Object> data = new ConcurrentHashMap<>();
    protected final UUID uniqueId;

    public PlayerAdapter(final UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public final UUID getUniqueId() {
        return uniqueId;
    }

    public final void removeData(String key) {
        this.data.remove(key);
    }

    public final boolean hasData(String key) {
        return data.containsKey(key);
    }

    public final boolean hasData(String key, Class<?> type) {
        Object value = data.get(key);
        return value != null && type.isAssignableFrom(value.getClass());
    }

    public final Object getData(String key) {
        return data.get(key);
    }

    public final Object getDataOrFallback(String key, Object fallback) {
        Object value = data.get(key);
        if (value == null) {
            return fallback;
        }
        return value;
    }

    public final <E> E getData(String key, Class<E> type) {
        Object value = this.data.get(key);
        if (value == null || !type.isAssignableFrom(value.getClass())) {
            return null;
        }
        return type.cast(value);
    }

    public final <E> E getDataOrFallback(String key, E fallback, Class<E> type) {
        Object value = this.data.get(key);
        if (value == null || !type.isAssignableFrom(value.getClass())) {
            return fallback;
        }
        return type.cast(value);
    }

    public final void setData(String key, Object data) {
        this.data.put(key, data);
    }

    public abstract Player asBukkit();

    public abstract int getPermissionLevel();

    public abstract void acknowledgeBlockChangesUpTo(int sequence);

    public abstract void send(AbstractPacketOut... packets);

}
