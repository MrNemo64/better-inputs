package me.nemo_64.betterinputs.bukkit.nms.v1_16_R3;

import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.netty.channel.Channel;
import me.nemo_64.betterinputs.bukkit.nms.PlayerAdapter;
import me.nemo_64.betterinputs.bukkit.nms.packet.AbstractPacketOut;
import me.nemo_64.betterinputs.bukkit.nms.v1_16_R3.network.PacketManager1_16_R3;
import me.nemo_64.betterinputs.bukkit.nms.v1_16_R3.network.PlayerNetwork1_16_R3;
import me.nemo_64.betterinputs.bukkit.nms.v1_16_R3.util.MinecraftConstant1_16_R3;
import net.minecraft.server.v1_16_R3.*;

public final class PlayerAdapter1_16_R3 extends PlayerAdapter {

    private final PlayerNetwork1_16_R3 network;

    private final CraftPlayer bukkit;
    private final EntityPlayer minecraft;

    public PlayerAdapter1_16_R3(PacketManager1_16_R3 packetManager, Player player) {
        super(player.getUniqueId());
        this.bukkit = (CraftPlayer) player;
        this.minecraft = bukkit.getHandle();
        this.network = new PlayerNetwork1_16_R3(packetManager, this);
    }

    final void terminate() {
        network.setActive(false);
    }

    public PlayerNetwork1_16_R3 getNetwork() {
        return network;
    }

    public EntityPlayer asMinecraft() {
        return minecraft;
    }

    @Override
    public int createAnvilMenu(String name, ItemStack itemStack) {
        if(!Bukkit.isPrimaryThread()) {
            return CompletableFuture.supplyAsync(() -> createAnvilMenu(name, itemStack), network.packetManager().mainService()).join();
        }
        ContainerAnvil menu = new ContainerAnvil(minecraft.nextContainerCounter(), minecraft.inventory, MinecraftConstant1_16_R3.BETTER_NULL);
        menu.setTitle(new ChatComponentText(name));
        menu.getSlot(0).set(CraftItemStack.asNMSCopy(itemStack));
        minecraft.activeContainer = menu;
        minecraft.playerConnection.sendPacket(new PacketPlayOutOpenWindow(menu.windowId, menu.getType(), menu.getTitle()));
        menu.addSlotListener(minecraft);
        minecraft.updateInventory(menu);
        return menu.windowId;
    }

    @Override
    public void reopenMenu() {
        Container menu = minecraft.activeContainer;
        minecraft.playerConnection.sendPacket(new PacketPlayOutOpenWindow(menu.windowId, menu.getType(), menu.getTitle()));
    }

    @Override
    public void closeMenu() {
        minecraft.playerConnection.sendPacket(new PacketPlayOutCloseWindow(minecraft.activeContainer.windowId));
        minecraft.o();
    }

    @Override
    public CraftPlayer asBukkit() {
        return bukkit;
    }

    @Override
    public int getPermissionLevel() {
        return minecraft.server.b(minecraft.getProfile());
    }

    @Override
    public void send(AbstractPacketOut... packets) {
        for (AbstractPacketOut packet : packets) {
            if (!(packet.asMinecraft() instanceof Packet)) {
                continue;
            }
            minecraft.playerConnection.sendPacket((Packet<?>) packet.asMinecraft());
        }
    }

    @Override
    public void acknowledgeBlockChangesUpTo(int sequence) {
        
    }

    public Channel getChannel() {
        return minecraft.playerConnection.networkManager.channel;
    }

}