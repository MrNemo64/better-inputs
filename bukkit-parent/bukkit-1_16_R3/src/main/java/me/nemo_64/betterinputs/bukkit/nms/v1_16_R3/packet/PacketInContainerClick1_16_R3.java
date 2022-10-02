package me.nemo_64.betterinputs.bukkit.nms.v1_16_R3.packet;

import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import me.nemo_64.betterinputs.bukkit.nms.packet.PacketInContainerClick;
import net.minecraft.server.v1_16_R3.PacketPlayInWindowClick;

public final class PacketInContainerClick1_16_R3 extends PacketInContainerClick {

    private final PacketPlayInWindowClick packet;

    public PacketInContainerClick1_16_R3(final PacketPlayInWindowClick packet) {
        this.packet = packet;
    }

    @Override
    public int getContainerId() {
        return packet.b();
    }

    @Override
    public int getSlot() {
        return packet.c();
    }

    @Override
    public ClickAction getAction() {
        switch (packet.g()) {
        case CLONE:
            return ClickAction.CLONE;
        case PICKUP_ALL:
            return ClickAction.PICKUP_ALL;
        case QUICK_CRAFT:
            return ClickAction.QUICK_CRAFT;
        case QUICK_MOVE:
            return ClickAction.QUICK_MOVE;
        case SWAP:
            return ClickAction.SWAP;
        case THROW:
            return ClickAction.THROW;
        case PICKUP:
        default:
            return ClickAction.PICKUP;
        }
    }

    @Override
    public ItemStack getItemStack() {
        return CraftItemStack.asCraftMirror(packet.f());
    }

    @Override
    public Object asMinecraft() {
        return packet;
    }

}