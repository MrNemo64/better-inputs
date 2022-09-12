package me.nemo_64.betterinputs.api.future;

public interface IFutureResult<T> {
    
    boolean isEmpty();

    boolean isNull();

    T getValue();

    boolean hasFailed();

    Throwable getThrowable();

}
