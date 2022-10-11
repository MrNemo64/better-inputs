package me.nemo_64.betterinputs.api.input;

import java.util.Objects;

import me.nemo_64.betterinputs.api.platform.IPlatformActor;
import me.nemo_64.betterinputs.api.platform.IPlatformKey;
import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;
import me.nemo_64.betterinputs.api.util.argument.NotEnoughArgumentsException;
import me.nemo_64.betterinputs.api.util.registry.AbstractUnique;

public abstract class InputFactory<V, I extends AbstractInput<V>> extends AbstractUnique {

    private final Class<V> inputType;

    /**
     * Creates a new input factory
     * 
     * @param  key                  the key of the factory
     * @param  inputType            the input type of the input provider type
     * 
     * @throws NullPointerException if either the {@code key} or {@code inputType}
     *                                  are {@code null}
     */
    public InputFactory(final IPlatformKey key, final Class<V> inputType) throws NullPointerException {
        super(key);
        this.inputType = Objects.requireNonNull(inputType, "Class inputType can't be null");
    }

    public final Class<V> getInputType() {
        return inputType;
    }

    /**
     * Creates a new input provider type
     * 
     * @param  actor                       the owning actor of the input provider
     *                                         type
     * @param  map                         the parameters to create the input
     *                                         provider type
     * 
     * @return                             the input provider type
     * 
     * @throws NullPointerException        if either the {@code actor} or
     *                                         {@code map} are {@code null}
     * @throws NotEnoughArgumentsException if the provided parameters were not
     *                                         enough for the input provider type
     */
    public final I build(final IPlatformActor<?> actor, final ArgumentMap map) throws NullPointerException, NotEnoughArgumentsException {
        Objects.requireNonNull(map, "ArgumentMap can't be null!");
        Objects.requireNonNull(actor, "IPlatformActor can't be null!");
        I value = provide(actor, map);
        map.throwIfMissing();
        return value;
    }

    /**
     * This is called once this input factory is unregistered from the api
     */
    public void onUnregister() {}

    /**
     * Creates a new input provider type instance
     * 
     * @param  actor the owning actor of the input provider type
     * @param  map   the parameters the create the input provider type
     * 
     * @return       the input provider type or {@code null} if the provider type
     *                   couldn't be created because of missing arguments
     */
    protected abstract I provide(final IPlatformActor<?> actor, final ArgumentMap map);

}
