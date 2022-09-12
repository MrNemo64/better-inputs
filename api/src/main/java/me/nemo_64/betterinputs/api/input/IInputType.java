package me.nemo_64.betterinputs.api.input;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public interface IInputType<V> {

    Future<InputResult<V>> future();

    default InputResult<V> join() {
        try {
            return future().get();
        } catch (InterruptedException | ExecutionException e) {
            return InputResult.<V>of(e);
        }
    }

}
