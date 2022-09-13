package me.nemo_64.betterinputs.nms;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractPlayerAdapter implements IPlayerAdapter {

    protected final ConcurrentHashMap<String, Object> data = new ConcurrentHashMap<>();
    protected final UUID uniqueId;

    public AbstractPlayerAdapter(final UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public final UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public final void removeData(String key) {
        this.data.remove(key);
    }

    @Override
    public final boolean hasData(String key) {
        return data.containsKey(key);
    }

    @Override
    public final boolean hasData(String key, Class<?> type) {
        Object value = data.get(key);
        return value != null && type.isAssignableFrom(value.getClass());
    }

    @Override
    public final Object getData(String key) {
        return data.get(key);
    }

    @Override
    public final Object getDataOrFallback(String key, Object fallback) {
        Object value = data.get(key);
        if (value == null) {
            return fallback;
        }
        return value;
    }

    @Override
    public final <E> E getData(String key, Class<E> type) {
        Object value = this.data.get(key);
        if (value == null || !type.isAssignableFrom(value.getClass())) {
            return null;
        }
        return type.cast(value);
    }

    @Override
    public final <E> E getDataOrFallback(String key, E fallback, Class<E> type) {
        Object value = this.data.get(key);
        if (value == null || !type.isAssignableFrom(value.getClass())) {
            return fallback;
        }
        return type.cast(value);
    }

    @Override
    public final void setData(String key, Object data) {
        this.data.put(key, data);
    }

}
