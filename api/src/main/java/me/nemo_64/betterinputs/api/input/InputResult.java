package me.nemo_64.betterinputs.api.input;

public final class InputResult<V> {

    public static <V> InputResult<V> of(V value) {
        return new InputResult<>(value, null);
    }

    public static <V> InputResult<V> of(Throwable throwable) {
        return new InputResult<>(null, throwable);
    }

    private final V value;
    private final Throwable throwable;

    private InputResult(final V value, final Throwable throwable) {
        this.value = value;
        this.throwable = throwable;
    }

    public boolean isSuccess() {
        return throwable == null;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public boolean isNull() {
        return value == null;
    }

    public V getValue() {
        return value;
    }

}
