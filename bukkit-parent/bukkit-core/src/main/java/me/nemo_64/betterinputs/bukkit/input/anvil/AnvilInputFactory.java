package me.nemo_64.betterinputs.bukkit.input.anvil;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.nemo_64.betterinputs.api.input.InputFactory;
import me.nemo_64.betterinputs.api.platform.IPlatformActor;
import me.nemo_64.betterinputs.api.platform.IPlatformKey;
import me.nemo_64.betterinputs.api.util.argument.ArgumentMap;
import me.nemo_64.betterinputs.bukkit.nms.VersionHandler;

public final class AnvilInputFactory extends InputFactory<String, AnvilInput> {

    private final VersionHandler versionHandler;
    private final AnvilPacketListener listener;

    public AnvilInputFactory(IPlatformKey key, VersionHandler versionHandler) {
        super(key, String.class);
        this.versionHandler = versionHandler;
        this.listener = new AnvilPacketListener(versionHandler.getPacketManager());
    }

    @Override
    public void onUnregister() { // This can't be reregistered rn; new Factory required
        versionHandler.getPacketManager().unregister(listener.getContainer());
    }

    @Override
    protected AnvilInput provide(IPlatformActor<?> actor, ArgumentMap map) {
        ItemStack itemStack = map.get("item", ItemStack.class).orElse(null);
        if (itemStack == null || itemStack.getType().isAir()) {
            String itemName = map.get("item", String.class).orElse(null);
            if (itemName != null) {
                itemStack = new ItemStack(Material.PAPER);
                ItemMeta meta = itemStack.getItemMeta();
                meta.setDisplayName(itemName);
                itemStack.setItemMeta(meta);
            }
        }
        return new AnvilInput(versionHandler, listener.getContainer(), map.get("name", String.class).orElse("Anvil Input"), itemStack);
    }

}
