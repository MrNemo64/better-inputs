package me.nemo_64.betterinputs.api.future;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

public interface IStagedFuture<V> {
    
    IStagedFuture<Void> thenAccept(Consumer<? super V> consumer);
    
    IStagedFuture<Void> thenAcceptAsync(Executor executor, Consumer<? super V> consumer);

}
