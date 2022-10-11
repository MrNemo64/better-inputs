package me.nemo_64.betterinputs.api.platform;

public interface IPlatformKeyProvider {

    /**
     * Gets the namespace of this key provider
     * 
     * @return the namespace
     */
    String getNamespace();

    /**
     * Gets or creates a platform key based on the provided key
     * 
     * @param  key the key associated with this namespace
     * 
     * @return     the platform key with the namespace and key
     */
    IPlatformKey getKey(String key);

    /**
     * Unregisters the key provider and all keys related to it
     */
    void unregisterAll();

}
