package me.nemo_64.betterinputs.api.platform;

public final class InvalidActor<H> implements IPlatformActor<H> {

    @SuppressWarnings("rawtypes")
    private static final InvalidActor INSTANCE = new InvalidActor();

    @SuppressWarnings("unchecked")
    public static <E> IPlatformActor<E> get() {
        return (IPlatformActor<E>) INSTANCE;
    }
    
    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public H getHandle() {
        return null;
    }

    @Override
    public <P> IPlatformActor<P> as(Class<P> clazz) {
        return get();
    }

    @Override
    public void sendMessage(String message) {}

}
