package me.nemo_64.betterinputs.api.util.argument;

import me.nemo_64.betterinputs.api.util.Option;

public interface IArgumentMap {

    boolean has(String key);

    boolean has(String key, Class<?> type);

    Option<Object> get(String key);

    <E> Option<E> get(String key, Class<E> type);

    Option<Class<?>> getClass(String key);

    <E> Option<Class<? extends E>> getClass(String key, Class<E> abstraction);

    IArgumentMap set(String key, Object value);

    IArgumentMap remove(String key);

    IArgumentMap clear();

    IArgumentMap clone();

    boolean isEmpty();

    int size();

}
