package me.nemo_64.betterinputs.api.input;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import me.nemo_64.betterinputs.api.BetterInputs;
import me.nemo_64.betterinputs.api.platform.IPlatformActor;
import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;

public final class InputBuilder<T> {

    private Consumer<Throwable> exceptionHandler;
    private BiConsumer<InputProvider<T>, String> cancelListener;

    private IPlatformActor<?> actor;
    private String type;

    private final Class<T> inputType;

    private final ArgumentMap map = new ArgumentMap();

    private final BetterInputs<?> api;

    public InputBuilder(BetterInputs<?> api, Class<T> inputType) {
        this.api = Objects.requireNonNull(api, "BetterInputs api can't be null");
        this.inputType = Objects.requireNonNull(inputType, "Class inputType can't be null");
    }

    /*
     * Setter
     */

    public InputBuilder<T> type(String type) {
        this.type = type;
        return this;
    }

    public InputBuilder<T> params(ArgumentMap map) {
        this.map.copyFrom(map);
        return this;
    }

    public InputBuilder<T> param(String name, Object value) {
        map.set(name, value);
        return this;
    }

    public InputBuilder<T> removeParam(String name) {
        map.remove(name);
        return this;
    }

    public InputBuilder<T> clearParams() {
        map.clear();
        return this;
    }

    public InputBuilder<T> actor(Object actor) {
        this.actor = api.getActor(actor).orElse(null);
        return this;
    }

    public InputBuilder<T> cancelListener(BiConsumer<InputProvider<T>, String> cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }

    public InputBuilder<T> exceptionHandler(Consumer<Throwable> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    /*
     * Getter
     */

    public Consumer<Throwable> exceptionHandler() {
        return exceptionHandler;
    }

    public BiConsumer<InputProvider<T>, String> cancelListener() {
        return cancelListener;
    }

    public Class<T> inputType() {
        return inputType;
    }

    public String type() {
        return type;
    }

    public IPlatformActor<?> actor() {
        return actor;
    }

    public ArgumentMap argumentMap() {
        return map;
    }

    /*
     * Done
     */

    public InputProvider<T> provide() {
        return api.createProvider(this);
    }

}
