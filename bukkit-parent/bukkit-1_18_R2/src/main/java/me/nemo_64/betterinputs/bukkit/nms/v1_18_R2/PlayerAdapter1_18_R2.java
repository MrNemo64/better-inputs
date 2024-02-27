package me.nemo_64.betterinputs.bukkit.nms.v1_18_R2;

import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.netty.channel.Channel;
import me.nemo_64.betterinputs.bukkit.nms.PlayerAdapter;
import me.nemo_64.betterinputs.bukkit.nms.packet.AbstractPacketOut;
import me.nemo_64.betterinputs.bukkit.nms.v1_18_R2.network.PacketManager1_18_R2;
import me.nemo_64.betterinputs.bukkit.nms.v1_18_R2.network.PlayerNetwork1_18_R2;
import me.nemo_64.betterinputs.bukkit.nms.v1_18_R2.util.MinecraftConstant1_18_R2;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;

public final class PlayerAdapter1_18_R2 extends PlayerAdapter {

    private final PlayerNetwork1_18_R2 network;

    private final CraftPlayer bukkit;
    private final ServerPlayer minecraft;

    public PlayerAdapter1_18_R2(PacketManager1_18_R2 packetManager, Player player) {
        super(player.getUniqueId());
        this.bukkit = (CraftPlayer) player;
        this.minecraft = bukkit.getHandle();
        this.network = new PlayerNetwork1_18_R2(packetManager, this);
    }

    final void terminate() {
        network.setActive(false);
    }

    public PlayerNetwork1_18_R2 getNetwork() {
        return network;
    }

    public ServerPlayer asMinecraft() {
        return minecraft;
    }

    @Override
    public int createAnvilMenu(String name, ItemStack itemStack) {
        if(!Bukkit.isPrimaryThread()) {
            return CompletableFuture.supplyAsync(() -> createAnvilMenu(name, itemStack), network.packetManager().mainService()).join();
        }
        AnvilMenu menu = new AnvilMenu(minecraft.nextContainerCounter(), minecraft.getInventory(), MinecraftConstant1_18_R2.BETTER_NULL);
        menu.setTitle(new TextComponent(name));
        menu.getSlot(0).set(CraftItemStack.asNMSCopy(itemStack));
        minecraft.containerMenu = menu;
        minecraft.connection.send(new ClientboundOpenScreenPacket(menu.containerId, menu.getType(), menu.getTitle()));
        minecraft.initMenu(menu);
        return menu.containerId;
    }

    @Override
    public void reopenMenu() {
        AbstractContainerMenu menu = minecraft.containerMenu;
        minecraft.connection.send(new ClientboundOpenScreenPacket(menu.containerId, menu.getType(), menu.getTitle()));
    }

    @Override
    public void closeMenu() {
        minecraft.connection.send(new ClientboundContainerClosePacket(minecraft.containerMenu.containerId));
        minecraft.doCloseContainer();
    }

    @Override
    public CraftPlayer asBukkit() {
        return bukkit;
    }

    @Override
    public int getPermissionLevel() {
        return minecraft.getServer().getProfilePermissions(minecraft.getGameProfile());
    }

    @Override
    public void send(AbstractPacketOut... packets) {
        for (AbstractPacketOut packet : packets) {
            if (!(packet.asMinecraft() instanceof Packet)) {
                continue;
            }
            minecraft.connection.send((Packet<?>) packet.asMinecraft());
        }
    }

    @Override
    public void acknowledgeBlockChangesUpTo(int sequence) {
        
    }

    public Channel getChannel() {
        return minecraft.connection.getConnection().channel;
    }

}