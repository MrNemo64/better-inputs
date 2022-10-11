package me.nemo_64.betterinputs.api.input;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import me.nemo_64.betterinputs.api.BetterInputs;
import me.nemo_64.betterinputs.api.platform.IPlatformActor;
import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;
import me.nemo_64.betterinputs.api.util.argument.NotEnoughArgumentsException;

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

    /**
     * Sets the requested input provider type
     * 
     * @param  type the input provider type key
     * 
     * @return      the same builder
     */
    public InputBuilder<T> type(String type) {
        this.type = type;
        return this;
    }

    /**
     * Sets the parameters for the input provider type creation
     * 
     * @param  map the parameters for the input provider type
     * 
     * @return     the same builder
     */
    public InputBuilder<T> params(ArgumentMap map) {
        this.map.copyFrom(map);
        return this;
    }

    /**
     * Sets a parameter for the input provider type creation
     * 
     * @param  name  the name of the parameter
     * @param  value the value of the parameter
     * 
     * @return       the same builder
     */
    public InputBuilder<T> param(String name, Object value) {
        map.set(name, value);
        return this;
    }

    /**
     * Deletes a parameter for the input provider type creation
     * 
     * @param  name the name of the parameter that should be deleted
     * 
     * @return      the same builder
     */
    public InputBuilder<T> removeParam(String name) {
        map.remove(name);
        return this;
    }

    /**
     * Deletes all parameters that were set for the input provider type creation
     * 
     * @return the same builder
     */
    public InputBuilder<T> clearParams() {
        map.clear();
        return this;
    }

    /**
     * Sets the owning actor of this input process
     * 
     * @param  actor the owning actor
     * 
     * @return       the same builder
     */
    public InputBuilder<T> actor(Object actor) {
        this.actor = api.getActor(actor).orElse(null);
        return this;
    }

    /**
     * Sets the cancel listener for this input process
     * 
     * @param  cancelListener the cancel listener
     * 
     * @return                the same builder
     */
    public InputBuilder<T> cancelListener(BiConsumer<InputProvider<T>, String> cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }

    /**
     * Sets the exception handler for this input process
     * 
     * @param  exceptionHandler the exception handler
     * 
     * @return                  the same builder
     */
    public InputBuilder<T> exceptionHandler(Consumer<Throwable> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    /*
     * Getter
     */

    /**
     * Gets the currently set exception handler
     * 
     * @return the exception handler
     */
    public Consumer<Throwable> exceptionHandler() {
        return exceptionHandler;
    }

    /**
     * Gets the currently set cancel listener
     * 
     * @return the cancel listener
     */
    public BiConsumer<InputProvider<T>, String> cancelListener() {
        return cancelListener;
    }

    /**
     * Gets the input type
     * 
     * @return the input type
     */
    public Class<T> inputType() {
        return inputType;
    }

    /**
     * Gets the input provider type key
     * 
     * @return the input provider type key
     */
    public String type() {
        return type;
    }

    /**
     * Gets the wrapped owning actor
     * 
     * @return the owning actor
     */
    public IPlatformActor<?> actor() {
        return actor;
    }

    /**
     * Gets the parameters for the input provider type creation
     * 
     * @return the parameters
     */
    public ArgumentMap argumentMap() {
        return map;
    }

    /*
     * Done
     */

    /**
     * Creates a new input provider based on this builder
     * 
     * @return                             the new input provider
     * 
     * @throws NullPointerException        if the set actor was invalid or wasn't
     *                                         set at all
     * @throws IllegalArgumentException    if there is no InputFactory with the set
     *                                         key
     * @throws NotEnoughArgumentsException if the provided parameters were not
     *                                         enough to create the requested input
     *                                         provider type
     */
    public InputProvider<T> provide() throws NullPointerException, IllegalArgumentException, NotEnoughArgumentsException {
        return api.createProvider(this);
    }

}
