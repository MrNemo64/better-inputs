package me.nemo_64.betterinputs.api.util.argument;

import java.util.HashMap;
import java.util.Objects;

import me.lauriichan.laylib.reflection.ClassUtil;
import me.nemo_64.betterinputs.api.util.Option;

public final class ArgumentMap implements IArgumentMap {

    private final HashMap<String, Object> map = new HashMap<>();

    public boolean has(String key) {
        return map.containsKey(key);
    }

    public boolean has(String key, Class<?> type) {
        Object object = map.get(key);
        return object != null && type.isInstance(object);
    }

    public Option<Object> get(String key) {
        return Option.of(map.get(key));
    }

    public <E> Option<E> get(String key, Class<E> type) {
        return get(key).filter(object -> type.isAssignableFrom(ClassUtil.toComplexType(object.getClass()))).map(type::cast);
    }

    @Override
    public Option<Class<?>> getClass(String key) {
        return Option.of(map.get(key)).filter(val -> val instanceof Class).map(val -> (Class<?>) val);
    }

    @Override
    public <E> Option<Class<? extends E>> getClass(String key, Class<E> abstraction) {
        return getClass(key).filter(clazz -> abstraction.isAssignableFrom(ClassUtil.toComplexType(clazz)))
            .map(clazz -> clazz.asSubclass(abstraction));
    }

    public ArgumentMap set(String key, Object value) {
        map.put(key, Objects.requireNonNull(value));
        return this;
    }

    public ArgumentMap remove(String key) {
        map.remove(key);
        return this;
    }

    public ArgumentMap clear() {
        map.clear();
        return this;
    }

    public ArgumentMap clone() {
        ArgumentMap clone = new ArgumentMap();
        map.putAll(map);
        return clone;
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public int size() {
        return map.size();
    }

}
