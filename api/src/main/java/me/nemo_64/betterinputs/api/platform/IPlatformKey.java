package me.nemo_64.betterinputs.api.platform;

public interface IPlatformKey {

    String getNamespace();

    String getKey();

    @Override
    String toString();

    default boolean equals(IPlatformKey key) {
        return key != null && toString().equals(key.toString());
    }

}
