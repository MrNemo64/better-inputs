package me.nemo_64.betterinputs.bukkit.nms.v1_20_R2;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import me.nemo_64.betterinputs.bukkit.nms.IServiceProvider;
import me.nemo_64.betterinputs.bukkit.nms.PlayerAdapter;
import me.nemo_64.betterinputs.bukkit.nms.VersionHandler;
import me.nemo_64.betterinputs.bukkit.nms.packet.*;
import me.nemo_64.betterinputs.bukkit.nms.packet.listener.PacketManager;
import me.nemo_64.betterinputs.bukkit.nms.v1_20_R2.network.PacketManager1_20_R2;
import me.nemo_64.betterinputs.bukkit.nms.v1_20_R2.packet.*;
import net.minecraft.network.protocol.game.*;

public final class VersionHandler1_20_R2 extends VersionHandler {

    private final PacketManager1_20_R2 packetManager;

    public VersionHandler1_20_R2(IServiceProvider provider) {
        super(provider);
        this.packetManager = new PacketManager1_20_R2(this);
    }

    @Override
    protected void onEnable(PluginManager pluginManager) {
        registerPackets();
        packetManager.close();
    }

    private void registerPackets() {
        // Incoming packets (nms)
        packetManager.register(ServerboundSetCommandBlockPacket.class, PacketInSetCommandBlock1_20_R2::new);
        packetManager.register(ServerboundUseItemOnPacket.class, PacketInUseItemOn1_20_R2::new);
        packetManager.register(ServerboundSwingPacket.class, PacketInSwingArm1_20_R2::new);
        packetManager.register(ServerboundRenameItemPacket.class, PacketInRenameItem1_20_R2::new);
        packetManager.register(ServerboundContainerClickPacket.class, PacketInContainerClick1_20_R2::new);
        packetManager.register(ServerboundContainerClosePacket.class, PacketInContainerClose1_20_R2::new);
        // Outgoing packets (nms)
        packetManager.register(ClientboundEntityEventPacket.class, PacketOutEntityEvent1_20_R2::new);
        packetManager.register(ClientboundBlockUpdatePacket.class, PacketOutBlockUpdate1_20_R2::new);
        packetManager.register(ClientboundBlockEntityDataPacket.class, PacketOutBlockEntityData1_20_R2::new);
        // Outgoing packets (adapter)
        packetManager.registerAdapter(PacketOutEntityEvent.class, PacketOutEntityEvent1_20_R2::new);
        packetManager.registerAdapter(PacketOutBlockUpdate.class, PacketOutBlockUpdate1_20_R2::new);
        packetManager.registerAdapter(PacketOutBlockEntityData.class, PacketOutBlockEntityData1_20_R2::new);
    }

    @Override
    public PacketManager getPacketManager() {
        return packetManager;
    }

    @Override
    protected PlayerAdapter createAdapter(Player player) {
        return new PlayerAdapter1_20_R2(packetManager, player);
    }

    @Override
    protected void terminateAdapter(PlayerAdapter adapter) {
        if (!(adapter instanceof PlayerAdapter1_20_R2)) {
            return;
        }
        ((PlayerAdapter1_20_R2) adapter).terminate();
    }

}