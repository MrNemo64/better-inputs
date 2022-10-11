package me.nemo_64.betterinputs.api;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import me.nemo_64.betterinputs.api.input.AbstractInput;
import me.nemo_64.betterinputs.api.input.InputBuilder;
import me.nemo_64.betterinputs.api.input.InputFactory;
import me.nemo_64.betterinputs.api.input.InputProvider;
import me.nemo_64.betterinputs.api.platform.IPlatformActor;
import me.nemo_64.betterinputs.api.platform.IPlatformKey;
import me.nemo_64.betterinputs.api.platform.IPlatformKeyProvider;
import me.nemo_64.betterinputs.api.util.Ref;
import me.nemo_64.betterinputs.api.util.argument.NotEnoughArgumentsException;
import me.nemo_64.betterinputs.api.util.tick.TickTimer;

public abstract class BetterInputs<P> {

    private static final Ref<BetterInputs<?>> PLATFORM = Ref.of();

    public static BetterInputs<?> getPlatform() {
        return PLATFORM.get();
    }

    protected final TickTimer inputTick = new TickTimer("InputTick");

    public BetterInputs() {
        if (PLATFORM.isPresent()) {
            throw new IllegalStateException("Platform is already registered");
        }
        PLATFORM.set(this).lock();
        inputTick.start();
    }

    /**
     * Converts a object into a platform identifiable object
     * 
     * @param  object the object to be converted
     * 
     * @return        the platform identifiable object or {@code null}
     */
    protected abstract P asPlatformIdentifiable(Object object);

    /**
     * Gets all known input factory keys as {@code java.lang.String}
     * 
     * @return the list of all keys
     */
    public abstract List<String> getKeys();

    /**
     * Gets the key provider for the provided object which is supposed to be a
     * platform identifiable object
     * 
     * @param  object                   the object that is supposed to be a platform
     *                                      identifiable object
     * 
     * @return                          an {@code java.lang.Optional} containing the
     *                                      key provider for the platform
     *                                      identifiable object or {@code null} if
     *                                      the object was not a platform
     *                                      identifiable object
     * 
     * @throws IllegalArgumentException if the platform identifiable is not valid
     *                                      for this platform
     */
    public final Optional<IPlatformKeyProvider> tryGetKeyProvider(Object object) {
        P identifyable = asPlatformIdentifiable(object);
        if (identifyable == null) {
            return Optional.empty();
        }
        return Optional.of(getKeyProvider(identifyable));
    }

    /**
     * Gets a key provider for the platform identifiable object
     * 
     * @param  platformIdentifiable     the platform identifiable object
     * 
     * @return                          the key provider for the
     *                                      {@code platformIdentifiable}
     * 
     * @throws NullPointerException     if the platform identifiable is {@code null}
     * @throws IllegalArgumentException if the platform identifiable is not valid
     *                                      for this platform
     */
    public abstract IPlatformKeyProvider getKeyProvider(P platformIdentifiable) throws NullPointerException, IllegalArgumentException;

    /**
     * Gets a wrapped instance of the provided actor
     * 
     * @param  <E>   the type of the actor
     * @param  actor the provided actor
     * 
     * @return       an {@code java.util.Optional} containing the wrapped actor or
     *                   {@code null} if the actor is not supported or if the actor
     *                   was {@code null}
     */
    public abstract <E> Optional<IPlatformActor<E>> getActor(E actor);

    /**
     * Gets a factory by key
     * 
     * @param  namespacedKey        the key of the input factory
     * 
     * @return                      an {@code java.util.Optional} containing the
     *                                  requested input factory or {@code null}
     * 
     * @throws NullPointerException if {@code namespacedKey} is {@code null}
     */
    public abstract Optional<InputFactory<?, ?>> getInputFactory(String namespacedKey);

    /**
     * Gets a factory by key and input type
     * 
     * @param  <T>                  the provided input type
     * @param  namespacedKey        the key of the input factory
     * @param  inputType            the input type provided by the input provider
     *                                  type that the factory is creating
     * 
     * @return                      an {@code java.util.Optional} containing the
     *                                  requested input factory or {@code null}
     * 
     * @throws NullPointerException if either {@code namespacedKey} or
     *                                  {@code inputType} are {@code null}
     */
    public abstract <T> Optional<InputFactory<T, ? extends AbstractInput<T>>> getInputFactory(String namespacedKey, Class<T> inputType)
        throws NullPointerException;

    /**
     * Registers a new input factory
     * 
     * @param  <T>                      the input type of the input provider type
     * @param  factory                  the factory for the input provider type
     * 
     * 
     * @throws NullPointerException     if {@code factory} is {@code null}
     * @throws IllegalArgumentException if the provided key of the factory was not
     *                                      created by this api
     * @throws IllegalStateException    if there is already a input factory with the
     *                                      provided key
     */
    public abstract <T> void registerInputFactory(InputFactory<T, ? extends AbstractInput<T>> factory)
        throws NullPointerException, IllegalArgumentException, IllegalStateException;

    /**
     * Unregisters a input factory based on the provided key
     * 
     * @param  platformKey              the key of the input factory
     * 
     * @return                          {@code true} if the input factory was
     *                                      successfully unregistered otherwise
     *                                      {@code false}
     * 
     * @throws NullPointerException     if no key is provided
     * @throws IllegalArgumentException if the provided key was not created by this
     *                                      api
     */
    public abstract boolean unregisterInputFactory(IPlatformKey platformKey) throws NullPointerException, IllegalArgumentException;

    /**
     * Creates a new input builder
     * 
     * @param  <T>  the provided input type
     * @param  type the input type class
     * 
     * @return      the new input builder
     */
    public final <T> InputBuilder<T> createInput(Class<T> type) {
        return new InputBuilder<>(this, type);
    }

    /**
     * Creates a new input provider based on the {@code builder}
     * 
     * @param  builder                     the input builder that describes the
     *                                         input process
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
    public final <T> InputProvider<T> createProvider(InputBuilder<T> builder)
        throws NullPointerException, IllegalArgumentException, NotEnoughArgumentsException {
        Objects.requireNonNull(builder, "InputBuilder can't be null");
        Objects.requireNonNull(builder.actor(), "Actor can't be null (Maybe invalid actor?)");
        InputFactory<T, ? extends AbstractInput<T>> factory = getInputFactory(builder.type(), builder.inputType())
            .orElseThrow(() -> new IllegalArgumentException("Invalid InputFactory type '" + builder.type() + "'!"));
        InputProvider<T> provider = new InputProvider<>(builder.actor(), inputTick, factory.build(builder.actor(), builder.argumentMap()),
            builder.exceptionHandler(), builder.cancelListener());
        updateInputStats(builder.type(), builder.inputType());
        return provider;
    }

    protected abstract void updateInputStats(String providerType, Class<?> inputType);

}
