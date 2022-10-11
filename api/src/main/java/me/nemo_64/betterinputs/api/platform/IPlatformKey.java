package me.nemo_64.betterinputs.api.platform;

public interface IPlatformKey {

    /**
     * Gets the namespace of the key
     * 
     * @return the namespace
     */
    String getNamespace();

    /**
     * Gets the key of the key
     * 
     * @return the key
     */
    String getKey();

    /**
     * Gets the full key as String
     * 
     * @return the full key
     */
    @Override
    String toString();

    default boolean equals(IPlatformKey key) {
        return key != null && toString().equals(key.toString());
    }

}
