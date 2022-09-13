package me.nemo_64.betterinputs.nms;

import java.util.UUID;

import org.bukkit.entity.Player;

import me.nemo_64.betterinputs.nms.packet.AbstractPacketAdapter;

public interface IPlayerAdapter {

    void removeData(String key);

    boolean hasData(String key);

    boolean hasData(String key, Class<?> type);

    Object getData(String key);

    Object getDataOrFallback(String key, Object fallback);

    <E> E getData(String key, Class<E> type);

    <E> E getDataOrFallback(String key, E fallback, Class<E> type);

    void setData(String key, Object object);

    UUID getUniqueId();

    Player asBukkit();

    int getPermissionLevel();

    void send(AbstractPacketAdapter... packets);

}
