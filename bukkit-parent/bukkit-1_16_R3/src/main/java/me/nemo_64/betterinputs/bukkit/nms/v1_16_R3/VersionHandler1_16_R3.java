package me.nemo_64.betterinputs.bukkit.nms.v1_16_R3;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import me.nemo_64.betterinputs.bukkit.nms.IServiceProvider;
import me.nemo_64.betterinputs.bukkit.nms.PlayerAdapter;
import me.nemo_64.betterinputs.bukkit.nms.VersionHandler;
import me.nemo_64.betterinputs.bukkit.nms.packet.*;
import me.nemo_64.betterinputs.bukkit.nms.packet.listener.PacketManager;
import me.nemo_64.betterinputs.bukkit.nms.v1_16_R3.network.PacketManager1_16_R3;
import me.nemo_64.betterinputs.bukkit.nms.v1_16_R3.packet.*;
import net.minecraft.server.v1_16_R3.*;

public final class VersionHandler1_16_R3 extends VersionHandler {

    private final PacketManager1_16_R3 packetManager;

    public VersionHandler1_16_R3(IServiceProvider provider) {
        super(provider);
        this.packetManager = new PacketManager1_16_R3(this);
    }

    @Override
    protected void onEnable(PluginManager pluginManager) {
        registerPackets();
        packetManager.close();
    }

    private void registerPackets() {
        // Incoming packets (nms)
        packetManager.register(PacketPlayInSetCommandBlock.class, PacketInSetCommandBlock1_16_R3::new);
        packetManager.register(PacketPlayInUseItem.class, PacketInUseItemOn1_16_R3::new);
        packetManager.register(PacketPlayInArmAnimation.class, PacketInSwingArm1_16_R3::new);
        packetManager.register(PacketPlayInItemName.class, PacketInRenameItem1_16_R3::new);
        packetManager.register(PacketPlayInWindowClick.class, PacketInContainerClick1_16_R3::new);
        packetManager.register(PacketPlayInCloseWindow.class, PacketInContainerClose1_16_R3::new);
        // Outgoing packets (nms)
        packetManager.register(PacketPlayOutEntityStatus.class, PacketOutEntityEvent1_16_R3::new);
        packetManager.register(PacketPlayOutBlockChange.class, PacketOutBlockUpdate1_16_R3::new);
        packetManager.register(PacketPlayOutTileEntityData.class, PacketOutBlockEntityData1_16_R3::new);
        // Outgoing packets (adapter)
        packetManager.registerAdapter(PacketOutEntityEvent.class, PacketOutEntityEvent1_16_R3::new);
        packetManager.registerAdapter(PacketOutBlockUpdate.class, PacketOutBlockUpdate1_16_R3::new);
        packetManager.registerAdapter(PacketOutBlockEntityData.class, PacketOutBlockEntityData1_16_R3::new);
    }

    @Override
    public PacketManager getPacketManager() {
        return packetManager;
    }

    @Override
    protected PlayerAdapter createAdapter(Player player) {
        return new PlayerAdapter1_16_R3(packetManager, player);
    }

    @Override
    protected void terminateAdapter(PlayerAdapter adapter) {
        if (!(adapter instanceof PlayerAdapter1_16_R3)) {
            return;
        }
        ((PlayerAdapter1_16_R3) adapter).terminate();
    }

}