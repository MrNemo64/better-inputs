package me.nemo_64.betterinputs.bukkit.nms;

import java.util.concurrent.ExecutorService;

import org.bukkit.plugin.Plugin;

public interface IServiceProvider {
    
    Plugin plugin();
    
    ExecutorService mainService();
    
    ExecutorService asyncService();

}
