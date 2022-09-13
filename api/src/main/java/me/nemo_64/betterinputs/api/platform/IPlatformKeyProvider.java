package me.nemo_64.betterinputs.api.platform;

public interface IPlatformKeyProvider {

    String getNamespace();

    IPlatformKey getKey(String key);
    
    void unregisterAll();

}
