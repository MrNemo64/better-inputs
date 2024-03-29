package me.nemo_64.betterinputs.bukkit.input.anvil;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.nemo_64.betterinputs.api.input.AbstractInput;
import me.nemo_64.betterinputs.api.input.InputProvider;
import me.nemo_64.betterinputs.api.input.modifier.AttemptModifier;
import me.nemo_64.betterinputs.api.platform.IPlatformActor;
import me.nemo_64.betterinputs.bukkit.nms.PlayerAdapter;
import me.nemo_64.betterinputs.bukkit.nms.VersionHandler;
import me.nemo_64.betterinputs.bukkit.nms.packet.listener.PacketContainer;

public final class AnvilInput extends AbstractInput<String> {

    private final VersionHandler versionHandler;

    private final PacketContainer listener;

    private final String name;
    private final ItemStack item;

    private int containerId = -1;

    public AnvilInput(final VersionHandler versionHandler, final PacketContainer listener, final String name, final ItemStack item) {
        this.versionHandler = versionHandler;
        this.listener = listener;
        this.name = name;
        this.item = item;
    }

    @Override
    protected void onStart(InputProvider<String> provider, IPlatformActor<?> actor) {
        if (containerId != -1) {
            throw new IllegalStateException("Can't start Input twice");
        }
        Player bukkitPlayer = actor.as(Player.class).getHandle();
        if (bukkitPlayer == null) {
            throw new IllegalStateException("Unsupported actor");
        }
        PlayerAdapter player = versionHandler.getPlayer(bukkitPlayer);
        player.setData("input", this);
        containerId = item == null ? player.createAnvilMenu(name) : player.createAnvilMenu(name, item);
        listener.addUser(player.getUniqueId());
    }

    @SuppressWarnings("unchecked")
    boolean complete(PlayerAdapter player) {
        AttemptModifier<String> modifier = (AttemptModifier<String>) provider().getModifier(AttemptModifier.class).orElse(null);
        String text = player.getData("text", String.class);
        if (modifier != null) {
            if (!modifier.attempt(text)) {
                modifier.sendMessage(provider().getActor());
                return false;
            }
        }
        clearData(player);
        listener.asyncService().submit(() -> completeValue(text));
        return true;
    }

    private final void clearData(PlayerAdapter player) {
        listener.removeUser(player.getUniqueId());
        player.removeData("input");
        player.removeData("text");
    }

    @Override
    protected boolean onCancel() {
        PlayerAdapter player = versionHandler.getPlayer(provider().getActor().as(Player.class).getHandle());
        clearData(player);
        listener.mainService().submit(() -> player.closeMenu());
        return true;
    }

}
